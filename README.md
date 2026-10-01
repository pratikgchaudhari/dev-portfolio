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

Edit `src/main/resources/application.properties` for your name, role, and introduction. Edit the About text in `src/main/resources/templates/home.html`. The profile and About timeline on the home page are based on the supplied resume. The essays remain sample content to replace before publishing. Colors and layout are in `src/main/resources/static/style.css`. Google Fonts is optional; local sans-serif fonts are used if unavailable.

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

`CONTENT_DIR` overrides the post directory. Keep it on persistent storage and back it up (Git works well). No admin UI is needed: publishing means adding a Markdown file to the server's content folder or redeploying the image with updated content. Use your host's HTTPS/reverse proxy and domain configuration for a public launch. The application has not been publicly deployed.

## Article likes and views

Each article has a heart button that toggles a reader's like, plus a view count. Both totals also appear on the Writing page. A random, HttpOnly, SameSite browser cookie remembers the reader for up to one year; no sign-in is required. Each browser can like an article once and remove that like later. Clearing cookies or using another browser creates a new reader identity, so these are anonymous engagement counts, not verified unique people.

A view is recorded when an article is open in a visible browser tab with JavaScript enabled. Reopening or refreshing the same article within 30 minutes does not add another view for that browser. Listing pages, metadata requests, and HEAD requests do not count. Drafts, future posts, and missing articles cannot receive likes or views. Without JavaScript the article and saved counts remain readable; reactions require JavaScript and cookies.

Counts and likes are stored in `data/engagement.mv.db`, outside the JAR and Markdown files. Keep this directory writable and persistent across deployments. `ENGAGEMENT_DB` changes the database path (for example, `/var/lib/portfolio/engagement`, without a file extension), and `ENGAGEMENT_DB_PASSWORD` optionally sets its password. The Docker commands above retain this data in a named volume. Run one application instance per database file. Stop the application before copying the database for backup; restoring that file restores the counters and reactions. Article filenames identify the counters, so renaming a slug starts a new article's engagement history.

The API uses `GET /api/articles/{slug}/stats`, `POST /api/articles/{slug}/views`, and `PUT /api/articles/{slug}/like` with JSON `{"liked": true}` or `{"liked": false}`. Writes require the reader cookie and `X-Portfolio-Request: 1`; the browser gets its cookie by opening an article. Like writes are idempotent, and transactions serialize updates per article. Tests use isolated H2 databases and do not change production counts.

## Structure

- `src/main/java/dev/portfolio`: controllers, post model, Markdown reader
- `src/main/resources/templates`: shared layout and pages
- `src/main/resources/static`: CSS and favicon
- `content/posts`: editable sample articles
- `src/test/java/dev/portfolio`: rendering, visibility, routing, and safety tests

## Bookshelf

Visit `/books` for current and finished reads. Edit `content/books.json` to add a title and its link to `currentlyReading` or `finished`; move a book between arrays when you finish it. Changes appear on the next request without a rebuild. Keep both arrays present, even when empty. Use trusted HTTPS book links.

The initial 23 finished titles and their exact URLs were imported from the public [GitHub profile README](https://github.com/pratikgchaudhari/pratikgchaudhari/blob/main/README.md) on September 27, 2026. This is a local copy, not an automatic GitHub sync. The Nvidia Way was added as the current read.

`BOOKS_FILE` can override the JSON file location. Docker includes it through the existing `COPY content content`; for live updates, mount the file and set `BOOKS_FILE` to its mounted path.

## About timeline

The home-page `#about` section tells the career story in reverse chronological order, from the current Nasdaq role back to education. Edit `src/main/resources/templates/home.html` to update milestones. `/about` redirects to the merged section so existing links continue to work. Dates and selected achievements come from the supplied resume. The original PDF and its contact details are not served by the application.
