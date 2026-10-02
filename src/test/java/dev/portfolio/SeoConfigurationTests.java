package dev.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;

class SeoConfigurationTests {
    @ParameterizedTest
    @ValueSource(strings = {"notnullpratik.dev", "ftp://example.com", "https://user:password@example.com",
            "https://example.com/path", "https://example.com?query=yes", "https://example.com#fragment", "https:///"})
    void rejectsInvalidPublicOrigins(String origin) {
        assertThatThrownBy(() -> service(origin, true)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void missingDomainDoesNotInventCanonicalUrlsOrPublishASitemap() {
        var seo = service("", true);
        assertThat(seo.home().canonicalUrl()).isNull();
        assertThat(seo.home().structuredData()).isNull();
        assertThat(seo.home().robots()).isEqualTo("noindex, follow");
        var crawl = new CrawlController(null, seo, 6);
        assertThat(crawl.robots()).doesNotContain("Sitemap:", "Disallow: /\n");
        assertThatThrownBy(crawl::sitemap).isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void previewsKeepCanonicalDomainButDisableIndexingAndSitemap() {
        var seo = service("https://notnullpratik.dev/", false);
        assertThat(seo.home().canonicalUrl()).isEqualTo("https://notnullpratik.dev/");
        assertThat(seo.home().robots()).isEqualTo("noindex, follow");
        var crawl = new CrawlController(null, seo, 6);
        assertThat(crawl.robots()).doesNotContain("Sitemap:");
        assertThatThrownBy(crawl::sitemap).isInstanceOf(ResponseStatusException.class);
    }

    private SeoService service(String origin, boolean indexing) {
        return new SeoService(origin, indexing, "Pratik Chaudhari", "Senior Software Engineer", new ObjectMapper());
    }
}
