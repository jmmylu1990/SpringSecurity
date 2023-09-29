package com.example.security.config;

import com.example.security.component.CustomAuthenticationFailureHandler;
import com.example.security.component.CustomLogoutSuccessHandler;
import com.example.security.component.ImageCodeValidateFilter;
import com.example.security.component.MobileAuthenticationConfig;
import com.example.security.repository.PersistentLoginsRepository;
import com.example.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@EnableWebSecurity       // 開啟 MVC Security 安全配置
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private SavedRequestAwareAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private ImageCodeValidateFilter imageCodeValidateFilter; // 自訂過濾器（圖形驗證碼校驗）

    @Autowired
    private MobileAuthenticationConfig mobileAuthenticationConfig; // 手機簡訊驗證碼認證方式的設定類

    @Autowired
    private PersistentTokenRepository tokenRepository;

    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;

    /**
     * 密碼編碼器，密碼不能明文儲存
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 使用 BCryptPasswordEncoder 密碼編碼器，該編碼器會將隨機產生的 salt 混入最终生成的密文中
        return new BCryptPasswordEncoder();
    }

    /**
     * 客製用戶認證管理器來實現用戶認證
     * 1. 提供使用者認證所需資訊（使用者名稱、密碼、目前使用者的資源權）
     * 2. 可採用記憶體儲存方式，也可能採用資料庫方式
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //採用內存儲存方式，用戶認證信息儲存在內存中
//        auth.inMemoryAuthentication()
//                .withUser("admin").password(passwordEncoder()
//                        .encode("123456")).roles("ADMIN");
        // 不再使用記憶體方式儲存使用者認證訊息，而是動態從資料庫中獲取
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 客製化基於 HTTP 請求的使用者存取控制
     * 1. 配置攔截的哪一些資源
     * 2. 設定資源所對應的角色權限
     * 3. 定義認證方式：HttpBasic、HttpForm
     * 4. 客製化登入頁面、登入請求地址、錯誤處理方式
     * 5. 自訂 Spring Security 過濾器等
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 啟動 form 表單登入
        http.formLogin()
                // 設定登入頁面的存取路徑，預設為 /login，GET 請求；該路徑不設限訪問
                .loginPage("/login/page")
                // 設定登入表單提交路徑，預設為 loginPage() 設定的路徑，POST 請求
                .loginProcessingUrl("/login/form")
                // 設定登入表單中的使用者名稱參數，預設為 username
                .usernameParameter("name")
                // 設定登入表單中的密碼參數，預設為 password
                .passwordParameter("pwd")
                // 認證成功處理，如果存在原始存取路徑，則重定向到該路徑；如果沒有，則重定向 /index
                //即設定使用者認證成功後重定向的位址。 這裡需要注意，該路徑是使用者直接存取登入頁面認證成功後重定向的路徑，
                // 如果是其他路徑跳到登入頁面認證成功後會重新導向到原始存取路徑。 可設定第二個參數為 true，使認證成功後始終重定向到該位址。
                //.defaultSuccessUrl("/index")
                // 認證失敗處理，重定向到指定位址，預設為 loginPage() + ?error；該路徑不設限訪問
                //.failureUrl("/login/page?error")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler);

        //這裡需要簡單介紹下Spring Security的授權方式，
        // 在Spring Security中角色屬於權限的一部分。
        // 對於角色ROLE_ADMIN的授權方式有兩種：hasRole("ADMIN")和hasAuthority("ROLE_ADMIN")，
        // 這兩種方式是等價的。 可能有人會疑惑，為什麼在資料庫中的角色名稱添加了ROLE_前綴，
        // 而 hasRole() 配置時不需要加ROLE_前綴，因為原始碼hasRole()在判斷權限時會自動在角色名前面加上ROLE_前綴，
        // 所以設定時不需要加上ROLE_前綴，同時這也要求 UserDetails 物件的權限集合中儲存的角色名稱要有ROLE_前綴。
        // 如果不希望符合這個前綴，那麼改為呼叫 hasAuthority() 方法即可。

        // 開啟基於 HTTP 請求存取控制
        http.authorizeRequests()
                // 以下訪問不需要任何權限，任何人都可以訪問
                .antMatchers("/login/page","/code/image", "/mobile/page", "/code/mobile").permitAll()
                // 以下存取需要 ROLE_ADMIN 權限
                .antMatchers("/admin/**").hasRole("ADMIN")
                // 以下存取需要 ROLE_USER 權限
                .antMatchers("/user/**").hasAuthority("ROLE_USER")
                .antMatchers("/test/**").permitAll()
                // 其它任何請求存取都需要先通過認證
                .anyRequest().authenticated();

        // 關閉 csrf 防護
        http.csrf().disable();

        // 將自訂過濾器（圖形驗證碼校驗）新增至 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(imageCodeValidateFilter, UsernamePasswordAuthenticationFilter.class);
        // 將手機簡訊驗證碼認證的配置與目前的設定綁定
        http.apply(mobileAuthenticationConfig);


        // 開啟 Remember-Me 功能
        http.rememberMe()
                // 指定登入時「記得我」的 HTTP 參數，預設為 remember-me
                .rememberMeParameter("remember-me")
                // 設定 Token 有效期為 200s，預設時長為 2 星期
                .tokenValiditySeconds(200)
                // 設定操作資料庫的Repository
                .tokenRepository(tokenRepository)
                // 指定 UserDetailsService 對象
                .userDetailsService(userDetailsService);

        // 開啟註銷登陸功能
        http.logout()
                // 使用者登出登入時造訪的 url，預設為 /logout
                .logoutUrl("/logout")
                // 使用者成功登出登入後重定向的位址，預設為 loginPage() + ?logout
                //.logoutSuccessUrl("/login/page?logout")
                // 不再使用 logoutSuccessUrl() 方法，使用自訂的成功登出登入處理器
                .logoutSuccessHandler(logoutSuccessHandler)
                // 指定用户注销登录时删除的 Cookie
                .deleteCookies("JSESSIONID")
                // 使用者登出登入時是否立即清除使用者的 Session，預設為 true
                .invalidateHttpSession(true)
                // 使用者登出登入時是否立即清除使用者認證資訊 Authentication，預設為 true
                .clearAuthentication(true);


    }

    /**
     * 客製化一些全域性的安全配置，例如：不攔截靜態資源的訪問
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 靜態資源的存取不需要攔截，直接放行
        web.ignoring().antMatchers("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
