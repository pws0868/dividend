package com.devidend01.scraper;

import com.devidend01.model.Company;
import com.devidend01.model.ScrapedResult;

// Scraper 인터페이스를 통해 사이트를 바꿔서 scrap 해도 코드 재사용 가능
public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
