package id.co.bpddiy.social.security;

import id.co.bpddiy.social.model.User;
import id.co.bpddiy.social.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService untuk load user dari database
 * Digunakan oleh Spring Security untuk authentication
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by username (bisa username atau email)
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // User bisa login dengan username atau email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)
                );

        return UserPrincipal.create(user);
    }

    /**
     * Load user by user ID
     * Digunakan oleh JWT Authentication Filter
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return UserPrincipal.create(user);
    }
}