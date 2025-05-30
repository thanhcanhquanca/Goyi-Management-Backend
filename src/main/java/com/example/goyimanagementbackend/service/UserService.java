package com.example.goyimanagementbackend.service;

import com.example.goyimanagementbackend.config.JwtUtil;
import com.example.goyimanagementbackend.dto.AuthResponseDTO;
import com.example.goyimanagementbackend.dto.LoginRequestDTO;
import com.example.goyimanagementbackend.dto.RegisterRequestDTO;
import com.example.goyimanagementbackend.dto.UpdateUserProfileDTO;
import com.example.goyimanagementbackend.entity.Permission;
import com.example.goyimanagementbackend.entity.Role;
import com.example.goyimanagementbackend.entity.User;
import com.example.goyimanagementbackend.repository.PermissionRepository;
import com.example.goyimanagementbackend.repository.RoleRepository;
import com.example.goyimanagementbackend.repository.UserRepository;
import com.example.goyimanagementbackend.security.CustomUserDetailsService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        System.out.println("Đăng nhập cho số điện thoại: " + loginRequest.getPhoneNumber());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getPhoneNumber(),
                            loginRequest.getPassword()
                    )
            );
            System.out.println("Xác thực thành công cho: " + loginRequest.getPhoneNumber());
        } catch (Exception e) {
            throw new RuntimeException("Đăng nhập thất bại");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getPhoneNumber());
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException(""));  // không tìm thấy người dùng

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(userDetails); // Tạo token mới mỗi lần đăng nhập

        List<String> permissions = permissionRepository.findByRoleRoleIdAndIsAllowedTrue(user.getRole().getRoleId())
                .stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toList());
        System.out.println("Quyền: " + permissions);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUserName(user.getUserName());
        response.setRole(user.getRole().getRoleName());
        response.setPermissions(permissions);
        response.setProfilePicture(user.getProfilePicture());
        response.setUserCode(user.getUserCode());
        return response;
    }

    @Transactional
    public User register(RegisterRequestDTO registerRequest) {
        System.out.println("Đăng ký cho số điện thoại: " + registerRequest.getPhoneNumber());
        if (userRepository.findByPhoneNumber(registerRequest.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        User user = new User();
        user.setUserName(generateRandomUsername());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(User.UserStatus.ACTIVE);

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò mặc định"));
        user.setRole(userRole);

        user.setUserCode(generateUserCode());

        String qrCodePath = generateQRCode(registerRequest.getPhoneNumber(), user.getUserCode());
        user.setQrCode(qrCodePath);

        System.out.println("Lưu người dùng: " + user.getUserName());
        user = userRepository.save(user);
        return user; // Không tạo token ở đây
    }

    @Transactional
    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với số điện thoại: " + phoneNumber));
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    private String generateQRCode(String phoneNumber, String userCode) {
        String qrContent = "Phone: " + phoneNumber + "; UserCode: " + userCode;
        String fileName = userCode + "_qrcode.png";
        String relativePath = "qrcodes/" + fileName;
        String fullPath = uploadDir + File.separator + relativePath;

        try {
            File dir = new File(uploadDir + File.separator + "qrcodes");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);
            Path path = Paths.get(fullPath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            System.out.println("Đã tạo mã QR tại: " + fullPath);
            return relativePath;
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Lỗi khi tạo mã QR: " + e.getMessage());
        }
    }

    private String generateUserCode() {
        String prefix = "@";
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(prefix);
        for (int i = 0; i < 18; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    private String generateRandomUsername() {
        SecureRandom random = new SecureRandom();
        int length = random.nextBoolean() ? 6 : 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder username = new StringBuilder("Go");
        for (int i = 0; i < length; i++) {
            username.append(characters.charAt(random.nextInt(characters.length())));
        }
        return username.toString();
    }



    /**
     * Cập nhật thông tin hồ sơ người dùng
     * @param phoneNumber Số điện thoại của người dùng (lấy từ JWT token)
     * @param updateRequest DTO chứa thông tin cần cập nhật (userName, email, profilePicture, coverPhoto)
     * @return User đã được cập nhật
     * @throws RuntimeException Nếu không tìm thấy người dùng hoặc email đã tồn tại
     * @throws IOException Nếu có lỗi khi lưu/xóa file ảnh
     */
    @Transactional
    public User updateUserProfile(String phoneNumber, UpdateUserProfileDTO updateRequest) throws IOException {
        // Tìm user theo phoneNumber
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với số điện thoại: " + phoneNumber));

        // Cập nhật userName nếu được cung cấp
        if (updateRequest.getUserName() != null && !updateRequest.getUserName().isBlank()) {
            if (updateRequest.getUserName().length() < 3 || updateRequest.getUserName().length() > 50) {
                throw new RuntimeException("Tên người dùng phải từ 3 đến 50 ký tự");
            }
            user.setUserName(updateRequest.getUserName());
        }

        // Cập nhật email nếu được cung cấp
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank()) {
            if (!isValidEmail(updateRequest.getEmail())) {
                throw new RuntimeException("Email không hợp lệ");
            }
            if (userRepository.findByEmail(updateRequest.getEmail()).isPresent() &&
                    !updateRequest.getEmail().equals(user.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng: " + updateRequest.getEmail());
            }
            user.setEmail(updateRequest.getEmail());
        }

        // Xử lý ảnh cá nhân
        if (updateRequest.getProfilePicture() != null && !updateRequest.getProfilePicture().isEmpty()) {
            validateImageFile(updateRequest.getProfilePicture());
            String profilePicturePath = saveFile(updateRequest.getProfilePicture(), user.getUserCode(), "profile");
            // Xóa ảnh cá nhân cũ nếu tồn tại
            if (user.getProfilePicture() != null && !user.getProfilePicture().isBlank()) {
                deleteFile(user.getProfilePicture());
            }
            user.setProfilePicture(profilePicturePath);
        }

        // Xử lý ảnh bìa
        if (updateRequest.getCoverPhoto() != null && !updateRequest.getCoverPhoto().isEmpty()) {
            validateImageFile(updateRequest.getCoverPhoto());
            String coverPhotoPath = saveFile(updateRequest.getCoverPhoto(), user.getUserCode(), "cover");
            // Xóa ảnh bìa cũ nếu tồn tại
            if (user.getCoverPhoto() != null && !user.getCoverPhoto().isBlank()) {
                deleteFile(user.getCoverPhoto());
            }
            user.setCoverPhoto(coverPhotoPath);
        }

        // Cập nhật thời gian
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Lưu file ảnh vào thư mục được cấu hình
     * @param file File ảnh được tải lên
     * @param userCode Mã người dùng để tạo tên file
     * @param type Loại ảnh (profile hoặc cover)
     * @return Đường dẫn tương đối của file đã lưu
     * @throws IOException Nếu có lỗi khi lưu file
     */
    private String saveFile(MultipartFile file, String userCode, String type) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = userCode + "_" + type + "_" + UUID.randomUUID() + fileExtension;
        String relativePath = "user/" + type + "/" + fileName;
        String fullPath = uploadDir + File.separator + relativePath;

        // Tạo thư mục nếu chưa tồn tại
        File dir = new File(uploadDir + File.separator + "user/" + type);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Lưu file
        Path path = Paths.get(fullPath);
        Files.write(path, file.getBytes());

        return relativePath;
    }

    /**
     * Xóa file ảnh cũ
     * @param relativePath Đường dẫn tương đối của file cần xóa
     */
    private void deleteFile(String relativePath) {
        try {
            Path path = Paths.get(uploadDir + File.separator + relativePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa file: " + e.getMessage());
        }
    }

    /**
     * Lấy đuôi file
     * @param fileName Tên file
     * @return Đuôi file (mặc định .jpg nếu không xác định được)
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * Kiểm tra định dạng file ảnh
     * @param file File cần kiểm tra
     * @throws RuntimeException Nếu định dạng không hợp lệ hoặc kích thước quá lớn
     */
    private void validateImageFile(MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!fileExtension.matches("\\.(jpg|jpeg|png|gif)$")) {
            throw new RuntimeException("Định dạng file không hợp lệ. Chỉ hỗ trợ: jpg, jpeg, png, gif");
        }
        if (file.getSize() > 900 * 1024 * 1024) { // Giới hạn 90MB
            throw new RuntimeException("Kích thước file không được vượt quá 5MB");
        }
    }



    /**
     * Kiểm tra định dạng email
     * @param email Email cần kiểm tra
     * @return true nếu email hợp lệ
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

}