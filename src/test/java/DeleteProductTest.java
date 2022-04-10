import com.github.javafaker.Faker;
import com.google.gson.Gson;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DeleteProductTest {
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
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    void DeleteProductPositiveTest() throws IOException {
        Response<Product> response = productService.getProductById(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(id));

        Response<ResponseBody> body = productService.deleteProduct(id).execute();
        assertThat(body.isSuccessful(), CoreMatchers.is(true));

        response = productService.getProductById(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
    }

    @Test
    void deleteProductNegativeIncorrectTest() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(999).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().charStream(), ErrorResponse.class);
        assertThat(errorResponse.getStatus(), equalTo(500));
    }
    @Test
    void deleteProductNegativeCorrectTest() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(999).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().charStream(), ErrorResponse.class);
        assertThat(errorResponse.getStatus(), equalTo(400));
        assertThat(errorResponse.getMessage(), equalTo("Product with id: 999 doesn't exist"));
    }
}

