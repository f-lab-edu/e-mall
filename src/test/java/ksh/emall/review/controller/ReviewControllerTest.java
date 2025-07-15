package ksh.emall.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ksh.emall.review.dto.request.ReviewRegisterRequestDto;
import ksh.emall.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReviewService reviewService;

    @DisplayName("제품의 리뷰를 조회하면 200번 응답을 받는다")
    @Test
    void findReviewsOfProduct1() throws Exception {
        //given
        given(reviewService.findReviewsOfProduct(any(), any(), any()))
            .willReturn(Page.empty());

        //when //then
        mockMvc.perform(
                get("/products/1/reviews")
                    .param("page", "0")
                    .param("size", "1")
                    .param("isAscending", "true")
                    .param("lastId", "1")
                    .param("productId", "1")
                    .param("lastScore", "3")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("제품의 리뷰를 조회할 때 마지막 리뷰의 점수와 등록일 모두 요청하면 400번 응답을 받는다")
    @ParameterizedTest
    @CsvSource(value = {"3,2025-01-01", ","}, nullValues = "")
    void findReviewsOfProduct2(String lastScore, String lastRegisterDate) throws Exception {
        //given
        String errorMessage = "이전 페이지 마지막 리뷰의 점수와 등록일 중 하나만 입력해야 합니다.";

        var builder = get("/products/1/reviews")
            .param("page", "0")
            .param("size", "1")
            .param("isAscending", "true")
            .param("lastId", "1")
            .param("productId", "1");

        if (lastScore != null) {
            builder.param("lastScore", lastScore);
        }
        if (lastRegisterDate != null) {
            builder.param("lastRegisterDate", lastRegisterDate);
        }

        //when //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @DisplayName("제품의 리뷰를 등록하면 200번 응답을 받는다")
    @Test
    void register1() throws Exception {
        //given
        String title = "최고의 제품";
        String body = "디자인 좋고 성능도 만족합니다 추천합니다.";
        var requestDto = new ReviewRegisterRequestDto(
            1L,
            title,
            body,
            5
        );

        //when //then
        mockMvc.perform(
                post("/products/1/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}
