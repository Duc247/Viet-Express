package vn.DucBackend.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.UserRepository;

import java.util.Collections;
import java.util.List;

/**
 * Custom UserDetailsService để load user từ database cho Spring Security
 * Hỗ trợ đăng nhập bằng username hoặc email
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Tìm theo username trước
        User user = userRepository.findByUsername(usernameOrEmail).orElse(null);
        
        // Nếu không tìm thấy, tìm theo email
        if (user == null) {
            List<User> usersByEmail = userRepository.searchByEmail(usernameOrEmail);
            if (!usersByEmail.isEmpty()) {
                // Lấy user có email khớp chính xác
                user = usersByEmail.stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(usernameOrEmail))
                        .findFirst()
                        .orElse(null);
            }
        }
        
        if (user == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại: " + usernameOrEmail);
        }

        // Kiểm tra user đã xác thực email chưa
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException("Tài khoản chưa được kích hoạt. Vui lòng xác thực email.");
        }

        // Lấy role name
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName)));
    }
}
