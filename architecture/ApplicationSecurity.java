import javax.sql.DataSource;

@configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    // configuration here

    @Autowired
    public void configure(AuthenticationManagerBuilder builder) {
        builder.jdbcAuthentication().dataSource(dataSource).withUser("dave")
        .password("secret").roles("USER");
    }
    
}