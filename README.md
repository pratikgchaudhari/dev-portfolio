# Personal portfolio & Markdown blog

A Java 17 / Spring Boot 3.5 application with Spring MVC, Thymeleaf, CommonMark, and an embedded H2 database for article engagement. Responsive, server-rendered pages; no JavaScript build or separate database server required.

## Run

Install JDK 17+ and Maven 3.6.3+. From this directory:

```sh
mvn spring-boot:run
```

Open http://localhost:8080. Run commands from this project directory so `content/posts` resolves correctly.

```sh
mvn test
mvn package
java -jar target/portfolio-1.0.0.jar
```

## Make it yours

Edit `src/main/resources/application.properties` for your name, role, and introduction. Edit the About text in `src/main/resources/templates/home.html`. The profile and About timeline on the home page are based on the supplied resume. Published articles live in `content/posts`. Colors and layout are in `src/main/resources/static/style.css`. Google Fonts is optional; local sans-serif fonts are used if unavailable.

## Appearance

Use the sun/moon switch in the header to choose light or dark mode. New visitors follow their device's color preference, including changes while the page is open. A manual choice is saved in browser local storage, persists across pages and visits, and syncs between open tabs. If storage is blocked, the switch still works for the current page. Without JavaScript, the site follows the system preference and hides the inactive switch.

Both palettes live in `src/main/resources/static/style.css`; the small `theme.js` script applies the preference before styles load to avoid a flash of the wrong theme. Bookshelf cards, the About timeline, article code blocks, and reactions use the same palette.

## Publish a post

Create `content/posts/my-first-post.md`:

```markdown
---
title: My first post
date: 2026-09-27
summary: A brief description of the article.
tag: Engineering
draft: false
---

Start writing here.

## A section

Markdown supports **bold**, *italics*, links, images, lists, blockquotes, fenced code blocks, and tables.
```

The filename becomes `/blog/my-first-post`. Use lowercase letters, numbers, and hyphens. Metadata uses a deliberately small `key: value` format (not full YAML): one value per line, optionally surrounded by quotes. Title, ISO date, and summary are required. Tag defaults to Notes. Drafts and future-dated posts are hidden, including their detail URLs; dates are compared in UTC. Files are read on every request, so saving a file immediately updates the site. Posts sort newest first. An empty folder shows an empty state. Invalid published content reports an explicit server-side error with the filename; validate posts locally before deployment.

Raw HTML is escaped and unsafe URL schemes are removed by the Markdown renderer. Only rendered Markdown is inserted as HTML; metadata uses escaped template output. Images can be placed in `src/main/resources/static/images` and referenced as `/images/example.png` (rebuild the JAR for new static assets). Code blocks are styled without syntax highlighting.

## Related articles ("Also see")

Add an optional `related` line inside a post's front matter to link to other articles on this site:

```markdown
related: generics-in-java, concurrent-collections-in-java
```

Use each target's filename without `.md`, separated by commas. A single slug works too. The article shows an **Also see** section beneath its content with each linked article's current title, summary, tag, and reading time. Links open in the same tab and appear in the order you specify. The section supports both light and dark mode and works without JavaScript.

Duplicates, self-links, and references that do not match a published article are omitted, including drafts and future-dated posts. Use slugs rather than full URLs or Markdown links. If no valid related articles remain, the section is hidden. Links are one-way; add a `related` line to the other post for a reciprocal link. Changes take effect on the next request without rebuilding the JAR.

## Article pagination

The Writing page shows six published articles per page, newest first. When there are more articles, matching Previous/Next links and a page indicator appear above and below the list. The top controls stay visible as you scroll. Following a link from either set lands at the top controls on the new page, ready to continue browsing. The article count shows the visible range and total. Page URLs can be bookmarked: `/blog` is the first page, and `/blog?page=2` is the second.

Set `POSTS_PER_PAGE` to a positive integer to change the page size, or edit `portfolio.posts-per-page` in `application.properties`. Drafts and future posts are excluded before pagination. Articles with the same date are ordered by filename. Page numbers below 1 or invalid numbers return HTTP 400; pages beyond the last available page return HTTP 404. An empty blog still has a valid first page with its empty state.

## Deploy

Run the packaged JAR on a Java-compatible host, or build the provided Docker image:

```sh
docker build -t personal-portfolio .
docker run --rm -p 8080:8080 -v portfolio-data:/app/data personal-portfolio
```

For editable content outside the image:

```sh
docker run --rm -p 8080:8080 -v portfolio-data:/app/data -v "$PWD/content/posts:/posts:ro" -e CONTENT_DIR=/posts personal-portfolio
```

`CONTENT_DIR` overrides the post directory. Keep it on persistent storage and back it up (Git works well). No admin UI is needed: publishing means adding a Markdown file to the server's content folder or redeploying the image with updated content. Use your host's HTTPS/reverse proxy and domain configuration for a public launch. Production deployment is managed separately from this repository.

## Search and sharing

The public origin defaults to `https://notnullpratik.dev`. Set `SITE_URL` to override it; use an absolute HTTP(S) origin without a path, query, or fragment. Canonical links and structured-data URLs use this setting, never the incoming Host header. Production indexing is enabled by default:

```sh
SITE_URL=https://notnullpratik.dev INDEXING_ENABLED=true java -jar target/portfolio-1.0.0.jar
```

For a preview or staging deployment, set `INDEXING_ENABLED=false`. Pages then emit `noindex, follow`, and `/sitemap.xml` returns 404. An explicitly empty `SITE_URL` has the same indexing behavior and omits absolute URL metadata. Crawlers can still read preview HTML to see its noindex instruction; robots.txt is not an access-control mechanism.

- Every main page has a description, a canonical URL, and Open Graph/X sharing metadata. Article titles and descriptions come from Markdown metadata. Each article-list page has its own canonical URL; `?page=1` points to `/blog`, and tracking parameters are excluded.
- `/sitemap.xml` lists the home page, bookshelf, all article-list pages, and published articles. It refreshes when Markdown changes and excludes drafts, future posts, API endpoints, and the old `/about` route. `/robots.txt` advertises the sitemap. Modification dates are omitted because filesystem timestamps can change during deployment.
- JSON-LD describes the website, the author's profile and social accounts, article collections, and individual blog posts. Article dates come from the existing `date` field; no modification dates, ratings, or publication times are invented. The author byline links to the About section, and `/about` permanently redirects there.
- The shared 1200×630 preview image is `src/main/resources/static/images/social-card.png`. It is used for social sharing, not presented as an article illustration in structured data. Regenerate it with `java tools/GenerateSocialCard.java`, or pass a name and domain as two arguments, then rebuild the JAR.
- Text responses use HTTP compression when supported by the client. Fonts load directly from the document head with preconnect hints. Markdown images retain their alt text and use lazy loading and asynchronous decoding.

After deploying the rebuilt JAR, verify ownership of `notnullpratik.dev` in [Google Search Console](https://search.google.com/search-console), submit `https://notnullpratik.dev/sitemap.xml`, and inspect the home page and an article. Use Google's [Rich Results Test](https://search.google.com/test/rich-results) to validate the deployed structured data. These changes prepare the application; deployment and Search Console submission are separate steps.

Implementation references: Google's [pagination guidance](https://developers.google.com/search/docs/specialty/ecommerce/pagination-and-incremental-page-loading), [sitemap guidance](https://developers.google.com/search/docs/crawling-indexing/sitemaps/build-sitemap), and [article structured-data guidance](https://developers.google.com/search/docs/appearance/structured-data/article).

## Article likes and views

Each article has a heart button that toggles a reader's like, plus a view count. Both totals also appear on the Writing page. A random, HttpOnly, SameSite browser cookie remembers the reader for up to one year; no sign-in is required. Each browser can like an article once and remove that like later. Clearing cookies or using another browser creates a new reader identity, so these are anonymous engagement counts, not verified unique people.

A view is recorded when an article is open in a visible browser tab with JavaScript enabled. Reopening or refreshing the same article within 30 minutes does not add another view for that browser. Listing pages, metadata requests, and HEAD requests do not count. Drafts, future posts, and missing articles cannot receive likes or views. Without JavaScript the article and saved counts remain readable; reactions require JavaScript and cookies.

Counts and likes are stored in `data/engagement.mv.db`, outside the JAR and Markdown files. Keep this directory writable and persistent across deployments. `ENGAGEMENT_DB` changes the database path (for example, `/var/lib/portfolio/engagement`, without a file extension), and `ENGAGEMENT_DB_PASSWORD` optionally sets its password. The Docker commands above retain this data in a named volume. Run one application instance per database file. Stop the application before copying the database for backup; restoring that file restores the counters and reactions. Article filenames identify the counters, so renaming a slug starts a new article's engagement history.

The API uses `GET /api/articles/{slug}/stats`, `POST /api/articles/{slug}/views`, and `PUT /api/articles/{slug}/like` with JSON `{"liked": true}` or `{"liked": false}`. Writes require the reader cookie and `X-Portfolio-Request: 1`; the browser gets its cookie by opening an article. Like writes are idempotent, and transactions serialize updates per article. Tests use isolated H2 databases and do not change production counts.

## Structure

- `src/main/java/dev/portfolio`: controllers, post model, Markdown reader
- `src/main/resources/templates`: shared layout and pages
- `src/main/resources/static`: CSS and favicon
- `content/posts`: editable Markdown articles
- `src/test/java/dev/portfolio`: rendering, visibility, routing, and safety tests

## Bookshelf

Visit `/books` for current and finished reads. Edit `content/books.json` to add a title and its link to `currentlyReading` or `finished`; move a book between arrays when you finish it. Changes appear on the next request without a rebuild. Keep both arrays present, even when empty. Use trusted HTTPS book links.

The initial 23 finished titles and their exact URLs were imported from the public [GitHub profile README](https://github.com/pratikgchaudhari/pratikgchaudhari/blob/main/README.md) on September 27, 2026. This is a local copy, not an automatic GitHub sync. The Nvidia Way was added as the current read.

`BOOKS_FILE` can override the JSON file location. Docker includes it through the existing `COPY content content`; for live updates, mount the file and set `BOOKS_FILE` to its mounted path.

## About timeline

The home-page `#about` section tells the career story in reverse chronological order, from the current Nasdaq role back to education. Edit `src/main/resources/templates/home.html` to update milestones. `/about` redirects to the merged section so existing links continue to work. Dates and selected achievements come from the supplied resume. The original PDF and its contact details are not served by the application.
