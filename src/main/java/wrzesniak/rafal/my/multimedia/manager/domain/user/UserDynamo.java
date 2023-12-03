package wrzesniak.rafal.my.multimedia.manager.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;

import static wrzesniak.rafal.my.multimedia.manager.domain.user.UserRole.USER;

@Data
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class UserDynamo {//} implements UserDetails {

    private String username;
    @JsonIgnore
    @ToString.Exclude
    private String password;

    private UserRole userRole;
    private boolean enabled;
    private LocalDateTime createdOn;

    public UserDynamo(String username, String password) {
        this.username = username;
        this.password = password;
        this.userRole = USER;
        this.enabled = true;
        this.createdOn = LocalDateTime.now();
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singleton(new SimpleGrantedAuthority(userRole.name()));
//    }
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
}
