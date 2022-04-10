import com.github.javafaker.Faker;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UpdateProductTest {
    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    int id;
    String titleOld;
    int priceOld;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() throws IOException {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        titleOld = response.body().getTitle();
        priceOld = response.body().getPrice();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    void updateProductPositiveTest() throws IOException {

        Response<Product> response = productService.getProductById(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), equalTo(titleOld));
        assertThat(response.body().getPrice(), equalTo(priceOld));
        assertThat(response.body().getCategoryTitle(), equalTo("Food"));

        String titleNew = faker.food().ingredient();
        int priceNew = priceOld + 1;
        product = new Product()
                .withId(id)
                .withTitle(titleNew)
                .withCategoryTitle("Food")
                .withPrice(priceNew);
        response = productService.modifyProduct(product)
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), equalTo(titleNew));
        assertThat(response.body().getPrice(), equalTo(priceNew));
        assertThat(response.body().getCategoryTitle(), equalTo("Food"));
    }

    @Test
    void createProductNegativeTest() throws IOException {
        product = new Product()
                .withId(999)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
        Response<Product> response = productService.modifyProduct(product)
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().charStream(), ErrorResponse.class);
        assertThat(errorResponse.getStatus(), equalTo(400));
        assertThat(errorResponse.getMessage(), equalTo("Product with id: 999 doesn't exist"));
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> response = productService.deleteProduct(id).execute();
    }
}

