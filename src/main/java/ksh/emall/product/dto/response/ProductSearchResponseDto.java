package ksh.emall.product.dto.response;

import ksh.emall.common.dto.response.PageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductSearchResponseDto {

    private PageResponseDto<ProductResponseDto> products;
    private List<String> brands;

    public static ProductSearchResponseDto of(
        PageResponseDto<ProductResponseDto> pageResponseDto,
        List<String> brands
    ) {
        return new  ProductSearchResponseDto(pageResponseDto, brands);
    }
}
