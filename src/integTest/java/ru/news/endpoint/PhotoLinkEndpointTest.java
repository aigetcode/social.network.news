package ru.news.endpoint;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.news.endpoint.mvc.SuccessResponse;
import ru.news.endpoint.util.PostgresContainerWrapper;

import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("integTest")
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integTest.yaml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PhotoLinkEndpointTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AmazonS3 client;

    @Container
    private static final PostgreSQLContainer<PostgresContainerWrapper> postgresContainer = new PostgresContainerWrapper();

    @DynamicPropertySource
    public static void initSystemParams(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    @Sql(value = "classpath:./sql/insert-post.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(encoding = "utf-8"))
    @Sql(scripts = "classpath:./sql/drop-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(encoding = "utf-8"))
    void createPhotoSuccess() throws Exception {
        doReturn(new PutObjectResult()).when(client).putObject(any());
        doNothing().when(client).deleteObject(any());

        MvcResult mvcResult;
        try (InputStream inputStream = new ClassPathResource("image/test.jpg").getInputStream()) {
            byte[] bytes = StreamUtils.copyToByteArray(inputStream);
            MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE, bytes);

            mvcResult = mvc.perform(multipart("/v1/posts/50b4c7dd-51aa-4062-9bbc-c8a42cb70e56/photos")
                            .file(file))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        SuccessResponse<List<Integer>> photoResult = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SuccessResponse.class);
        for (Integer photoId : photoResult.getResponse()) {
            mvc.perform(delete("/v1/posts/50b4c7dd-51aa-4062-9bbc-c8a42cb70e56/photos/" + photoId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @Sql(value = "classpath:./sql/insert-post.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(encoding = "utf-8"))
    @Sql(scripts = "classpath:./sql/drop-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(encoding = "utf-8"))
    void getPhotosSuccess() throws Exception {
        mvc.perform(get("/v1/posts/50b4c7dd-51aa-4062-9bbc-c8a42cb70e56/photos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray());
    }
}
