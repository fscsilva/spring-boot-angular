package com.silva.industries.microservices.web.config;

import com.google.common.base.Predicates;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.path}")
    private String host;

    public static final String SWAGGER_API = "/v2/api-docs";
    public static final String SWAGGER_CONFIG_UI = "/configuration/ui";
    public static final String SWAGGER_RESOURCES = "/swagger-resources/**";
    public static final String SWAGGER_CONFIG_SECURITY = "/configuration/security";
    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String SWAGGER_WEBJARS = "/webjars/**";

    private static final String IGNORE_PATH = "^(/actuator.*)|(/error.*)$";
    private static final String HTTP_REGEX = "(https?:\\/\\/)?";
    private static final String PARAMETER_TYPE_HEADER = "header";
    private static final String PARAMETER_STRING_TYPE = "string";

    private static final Pair<String, String> AUTH_HEADER =
            ImmutablePair.of("Authorization", "Bearer");


    @Bean
    public Docket api() {

        ParameterBuilder authParameterBuilder = new ParameterBuilder()
                .name(AUTH_HEADER.getKey())
                .modelRef(new ModelRef(PARAMETER_STRING_TYPE))
                .parameterType(PARAMETER_TYPE_HEADER)
                .required(true)
                .description(AUTH_HEADER.getKey())
                .defaultValue(AUTH_HEADER.getValue());
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex(IGNORE_PATH)))
                .build()
                .host(host.replaceAll(HTTP_REGEX, StringUtils.EMPTY))
                .globalOperationParameters(Arrays.asList(authParameterBuilder.build()));
    }

}
