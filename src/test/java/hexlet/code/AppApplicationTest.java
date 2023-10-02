package hexlet.code;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
// При тестировании можно вообще не запускать сервер Spring будет обрабатывать HTTP запрос и направлять его в контроллер
// Код вызывается точно так же, как если бы он обрабатывал настоящий запрос
// Такие тесты обходятся дешевле в плане ресурсов. Для этого нужно внедрить MockMvc
@AutoConfigureMockMvc
// Чтобы исключить влияние тестов друг на друга, каждый тест будет выполняться в транзакции.
// После завершения теста транзакция автоматически откатывается
@Transactional
public class AppApplicationTest {
    // Автоматическое создание экземпляра класса MockMvc
    @Autowired
    private MockMvc mockMvc;
    @Test
    void testRootPage() throws Exception {
        // Выполняем запрос и получаем ответ
        MockHttpServletResponse response = mockMvc
                .perform(get("/welcome")) // Выполняем GET запрос по указанному адресу
                .andReturn() // Получаем результат MvcResult
                .getResponse(); // Получаем ответ MockHttpServletResponse из класса MvcResult

        assertThat(response.getStatus()).isEqualTo(200); // Проверяем статус ответа
        // Проверяем, что ответ содержит определенный текст
        assertThat(response.getContentAsString()).contains("Welcome to Spring");
    }
}
