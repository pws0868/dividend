package com.devidend01.scheduler;

import com.devidend01.model.Company;
import com.devidend01.model.ScrapedResult;
import com.devidend01.model.constants.CacheKey;
import com.devidend01.persist.CompanyRepository;
import com.devidend01.persist.DividendRepository;
import com.devidend01.persist.entitiy.CompanyEntity;
import com.devidend01.persist.entitiy.DividendEntity;
import com.devidend01.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException {
//        Thread.sleep(10000);
//        System.out.println(Thread.currentThread().getName() + "테스트 1 :" + LocalDateTime.now());
//    }
//    @Scheduled(fixedDelay = 1000)
//    public void test2() {
//        System.out.println(Thread.currentThread().getName() +"테스트 2 :" + LocalDateTime.now());
//    }

    // 일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) //스케쥴러 동작시 수행되며, 캐시 데이터를 초기화 해준다
    @Scheduled(cron = "${scheduler.scrap.yahoo}") //application.properties 에서 관리
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started ->" + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                                                            new Company(company.getTicker(), company.getName()));


            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        // 데이터베이스에 있는지 없는지에 대한 검증
                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend->" + e.toString());
                            //존재 하지 않는 경우 dividendRepository 에 저장
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 하지 않도록 일시정지
            try {
                Thread.sleep(3000); //3초
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
