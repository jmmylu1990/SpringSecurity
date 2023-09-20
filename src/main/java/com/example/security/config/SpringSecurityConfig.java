package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity       // 開啟 MVC Security 安全配置
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

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
        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder()
                        .encode("123456")).roles("ADMIN");
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
                .defaultSuccessUrl("/index")
                // 認證失敗處理，重定向到指定位址，預設為 loginPage() + ?error；該路徑不設限訪問
                .failureUrl("/login/page?error");

        // 開啟基於 HTTP 請求存取控制
        http.authorizeRequests()
                // 以下訪問不需要任何權限，任何人都可以訪問
                .antMatchers("/login/page").permitAll()
                // 其它任何请求访问都需要先通过认证
                .anyRequest().authenticated();

        // 關閉 csrf 防護
        http.csrf().disable();
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
