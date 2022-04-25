package id.ac.ui.cs.workout_tracker.config;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineSignatureValidator;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;



@Configuration
@PropertySource("classpath:application.properties")
public class Config {

    @Autowired
    private Environment venv;


    /** Get Channel Secret.
     */
    @Bean(name = "com.linecorp.channel_secret")
    public String getChannelSecret() {
        return venv.getProperty("com.linecorp.channel_secret");
    }

    /** Get Channel Access Token.
     */
    @Bean(name = "com.linecorp.channel_access_token")
    public String getChannelAccessToken() {
        return venv.getProperty("com.linecorp.channel_access_token");
    }

    /** Get Messaging Client.
     */
    @Bean(name = "lineMessagingClient")
    public LineMessagingClient getMessagingClient() {
        return LineMessagingClient
                .builder(getChannelAccessToken())
                .apiEndPoint("https://api.line.me/")
                .connectTimeout(10_000)
                .readTimeout(10_000)
                .writeTimeout(10_000)
                .build();
    }

    /** Get Signature Validator.
     */
    @Bean(name = "lineSignatureValidator")
    public LineSignatureValidator getSignatureValidator() {
        return new LineSignatureValidator(getChannelSecret().getBytes());
    }


}