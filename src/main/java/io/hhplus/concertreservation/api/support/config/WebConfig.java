package io.hhplus.concertreservation.api.support.config;


import io.hhplus.concertreservation.api.interceptor.QueueVerificationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private QueueVerificationInterceptor queueVerificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 특정 URL 패턴에 인터셉터 적용
        registry.addInterceptor(queueVerificationInterceptor)
                .addPathPatterns("/api/reservation1/payment");
    }
}