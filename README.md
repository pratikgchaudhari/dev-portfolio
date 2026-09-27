# Personal portfolio & Markdown blog

A Java 17 / Spring Boot 3.5 application with Spring MVC, Thymeleaf, and CommonMark. Responsive, server-rendered pages; no JavaScript build or database required.

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

## Deploy

Run the packaged JAR on a Java-compatible host, or build the provided Docker image:

```sh
docker build -t personal-portfolio .
docker run --rm -p 8080:8080 personal-portfolio
```

For editable content outside the image:

```sh
docker run --rm -p 8080:8080 -v "$PWD/content/posts:/posts:ro" -e CONTENT_DIR=/posts personal-portfolio
```

`CONTENT_DIR` overrides the post directory. Keep it on persistent storage and back it up (Git works well). No admin UI is needed: publishing means adding a Markdown file to the server's content folder or redeploying the image with updated content. Use your host's HTTPS/reverse proxy and domain configuration for a public launch. The application has not been publicly deployed.

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
