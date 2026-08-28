package ai.startupforge.auth.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserAccountRepository extends JpaRepository<UserAccount, java.util.UUID> {
    Optional<UserAccount> findByEmail(String email);
}
