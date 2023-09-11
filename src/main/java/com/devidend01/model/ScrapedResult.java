package com.devidend01.model;

import com.devidend01.persist.CompanyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor //초기화를 시키는 생성자를 자동 생성
public class ScrapedResult {
    private Company company;
    private List<Dividend> dividends;
    public ScrapedResult() {
        this.dividends = new ArrayList<>();
    }
}
