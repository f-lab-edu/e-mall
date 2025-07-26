package ksh.emall.product.controller;

import ksh.emall.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @DisplayName("카테고리별 상품을 조회하면 200번 응답을 받는다")
    @Test
    void findProductsByCategory1() throws Exception {
        //given
        given(productService.findProductsByCategory(any(), any()))
            .willReturn(Page.empty());

        //when //then
        mockMvc.perform(
                get("/products")
                    .param("page", "0")
                    .param("size", "1")
                    .param("category", "FOOD")
                    .param("criteria", "PRICE")
                    .param("isAscending", "true")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("카테고리별 상품을 검색하면 200번 응답을 받는다")
    @Test
    void searchProductsWithConditions1() throws Exception {
        //given
        given(productService.searchProducts(any(), any(), any()))
            .willReturn(Page.empty());

        //when //then
        mockMvc.perform(
                get("/products/search")
                    .param("page", "0")
                    .param("size", "1")
                    .param("category", "FOOD")
                    .param("criteria", "PRICE")
                    .param("isAscending", "true")
                    .param("searchKeyword", "콜라")
                    .param("brands", "브랜드1", "브랜드2", "브랜드3")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("카테고리별 상품을 검색 시 최소 희망 가격이 최대 희망 가격 이상이면 400번 응답을 받는다")
    @Test
    void searchProductsWithConditions2() throws Exception {
        //given
        String errorMessage = "최소 희망 가격은 최대 희망 가격보다 클 수 없습니다.";

        //when //then
        mockMvc.perform(
                get("/products/search")
                    .param("page", "0")
                    .param("size", "1")
                    .param("category", "FOOD")
                    .param("criteria", "PRICE")
                    .param("isAscending", "true")
                    .param("searchKeyword", "콜라")
                    .param("minPrice", "1000")
                    .param("maxPrice", "1000")
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }
}
