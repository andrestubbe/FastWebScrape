package fastscrape;

import fastansi.FastANSI;
import fastspider.FastSpider;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * FastScrape — High-Speed Native AVX2 HTML Scraping & CleanText Hero Demo.
 * Powered by FastSpider (Native WinHTTP Session) for multi-node web ingestion and AVX2 for zero-copy text mining.
 */
public class Demo {

    private Demo() {}

    private static final List<String> TARGET_PAGES = List.of(
        "https://en.wikipedia.org/wiki/SIMD",
        "https://en.wikipedia.org/wiki/Advanced_Vector_Extensions",
        "https://en.wikipedia.org/wiki/AVX-512",
        "https://en.wikipedia.org/wiki/Streaming_SIMD_Extensions",
        "https://en.wikipedia.org/wiki/ARM_architecture_family",
        "https://en.wikipedia.org/wiki/RISC-V",
        "https://en.wikipedia.org/wiki/Graphics_processing_unit",
        "https://en.wikipedia.org/wiki/General-purpose_computing_on_graphics_processing_units",
        "https://en.wikipedia.org/wiki/CUDA",
        "https://en.wikipedia.org/wiki/OpenCL",
        "https://en.wikipedia.org/wiki/Java_(programming_language)",
        "https://en.wikipedia.org/wiki/C%2B%2B",
        "https://en.wikipedia.org/wiki/Rust_(programming_language)",
        "https://en.wikipedia.org/wiki/Go_(programming_language)",
        "https://en.wikipedia.org/wiki/Julia_(programming_language)",
        "https://en.wikipedia.org/wiki/Fortran",
        "https://en.wikipedia.org/wiki/Assembly_language",
        "https://en.wikipedia.org/wiki/Compiler",
        "https://en.wikipedia.org/wiki/Just-in-time_compilation",
        "https://en.wikipedia.org/wiki/Parallel_computing"
    );

    public static void main(String[] args) throws Exception {
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println(" " + boldWhite("FastScrape") + darkGray(" — Real-Time Native AVX2 HTML Scraping & CleanText Pipeline"));
        System.out.println(darkGray(" INGESTION: FastSpider WinHTTP Native Session  |  PARSER: AVX2 Zero-Copy Tag Stripper & Link Harvester"));
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println();

        FastScrape scraper = FastScrape.open();
        FastSpider spider = FastSpider.open();

        // ── Phase 1: High-Speed WinHTTP Multi-Node Ingestion ─────────────────
        System.out.println(darkGray("[Phase 1]") + " " + boldWhite("FastSpider Native WinHTTP Ingestion") + darkGray(" (Downloading 20 heavy Wikipedia nodes)"));

        long fetchT0 = System.currentTimeMillis();
        List<CompletableFuture<FastSpider.SpiderResponse>> futures = new ArrayList<>();
        for (String url : TARGET_PAGES) {
            futures.add(spider.fetchAsync(url));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long fetchDuration = System.currentTimeMillis() - fetchT0;

        List<byte[]> payloads = new ArrayList<>();
        long totalRawBytes = 0;
        for (CompletableFuture<FastSpider.SpiderResponse> f : futures) {
            byte[] body = f.join().rawBody();
            payloads.add(body);
            totalRawBytes += body.length;
        }

        double mbTotal = totalRawBytes / (1024.0 * 1024.0);
        double mbPerSec = mbTotal / (Math.max(fetchDuration, 1) / 1000.0);

        System.out.printf("  %s %s across %s in %s (%s)\n\n",
                darkGray("└── Ingested"),
                boldWhite(String.format("%.2f MB raw HTML", mbTotal)),
                boldWhite("20 live nodes"),
                boldWhite(String.format("%,d ms", fetchDuration)),
                darkGray(String.format("%.1f MB/s via WinHTTP", mbPerSec)));

        // ── Phase 2: Microsecond Real-Time SIMD Parsing Stream ──────────────
        System.out.println(darkGray("[Phase 2]") + " " + boldWhite("AVX2 Extraction & CleanText Content Stream") + darkGray(" (Stripping tags, titles, links & LLM paragraphs)"));
        System.out.println();

        long totalParseNanos = 0;
        long totalCleanChars = 0;
        int totalLinksExtracted = 0;
        List<String> cleanDocuments = new ArrayList<>();

        for (int i = 0; i < TARGET_PAGES.size(); i++) {
            String url = TARGET_PAGES.get(i);
            byte[] html = payloads.get(i);
            String shortUrl = truncate(url, 48);

            long parseT0 = System.nanoTime();
            String cleanText = scraper.extractReadableText(html);
            List<String> rawLinks = scraper.extractLinks(html);
            List<String> validLinks = filterWikiArticleLinks(rawLinks);
            List<String> rawHeadings = scraper.extractByTag(html, "h1");
            List<String> rawH2s = scraper.extractByTag(html, "h2");
            long parseUs = (System.nanoTime() - parseT0) / 1000;

            totalParseNanos += (System.nanoTime() - parseT0);
            totalCleanChars += cleanText.length();
            totalLinksExtracted += validLinks.size();
            cleanDocuments.add(cleanText);

            double reductionPct = 100.0 * (html.length - cleanText.length()) / html.length;

            // Extract clean title without inner tags
            String rawH1 = rawHeadings.isEmpty() ? "" : rawHeadings.get(0);
            String cleanH1 = rawH1.isEmpty() ? extractPageTitleFromUrl(url) : scraper.extractReadableText(rawH1.getBytes(StandardCharsets.UTF_8)).trim();
            if (cleanH1.isEmpty()) cleanH1 = extractPageTitleFromUrl(url);

            System.out.printf("  ├── %s %-48s %s %s %s %s\n",
                    boldWhite(String.format("[%02d]", i + 1)),
                    white(shortUrl),
                    darkGray(String.format("| %,6d KB HTML", html.length / 1024)),
                    boldWhite(String.format("| %,6d µs", parseUs)),
                    darkGray(String.format("| %,5d links", validLinks.size())),
                    darkGray(String.format("| -%.1f%% noise", reductionPct)));

            // 1. Title line
            System.out.printf("  │    ├── %s %s\n", darkGray("Title:   "), boldWhite(truncate(cleanH1, 70)));

            // 2. Sub-headings (H2) overview
            List<String> cleanH2List = sanitizeHeadings(scraper, rawH2s);
            if (!cleanH2List.isEmpty()) {
                String h2Joined = String.join(" • ", cleanH2List.subList(0, Math.min(cleanH2List.size(), 4)));
                System.out.printf("  │    ├── %s %s\n", darkGray("Sections:"), darkGray(truncate(h2Joined, 85)));
            }

            // 3. Extract genuine encyclopedic paragraph for LLM
            String summarySnippet = findFirstEncyclopedicParagraph(cleanText, cleanH1);
            if (!summarySnippet.isEmpty()) {
                System.out.printf("  │    ├── %s %s\n", darkGray("Synopsis:"), white(truncate(summarySnippet, 95)));
            }

            // 4. Stream 3 clean article links
            int previewCount = Math.min(validLinks.size(), 3);
            for (int p = 0; p < previewCount; p++) {
                boolean isLast = (p == previewCount - 1);
                String lk = truncate(validLinks.get(p), 65);
                System.out.printf("  │    %s %s %s\n",
                        darkGray(isLast ? "└──" : "├──"),
                        darkGray(String.format("[LINK %02d]", p + 1)),
                        darkGray(lk));
            }
        }
        System.out.println();

        // ── Phase 3: High-Fidelity LLM Context Preview (Multi-Node Corpus) ──
        System.out.println(darkGray("[Phase 3]") + " " + boldWhite("LLM Knowledge Corpus Context Sample") + darkGray(" (Extracted encyclopedic knowledge blocks)"));
        System.out.println();

        int[] sampleIndices = {0, 1, 6}; // SIMD, AVX, GPU
        for (int idx : sampleIndices) {
            if (idx < cleanDocuments.size()) {
                String nodeUrl = TARGET_PAGES.get(idx);
                String doc = cleanDocuments.get(idx);
                String nodeTitle = extractPageTitleFromUrl(nodeUrl);
                String bodyPara = findFirstEncyclopedicParagraph(doc, nodeTitle);
                int estTokens = bodyPara.length() / 4;

                System.out.printf("  %s %s %s\n",
                        boldWhite(String.format("◆ [%s]", nodeTitle)),
                        darkGray("—"),
                        darkGray(String.format("~%d prompt tokens", estTokens)));
                System.out.printf("  │  %s\n", white(truncate(bodyPara, 114)));
                System.out.println("  │");
            }
        }

        // ── Performance Summary Card ─────────────────────────────────────────
        long totalParseMs = totalParseNanos / 1_000_000;
        double overallReduction = 100.0 * (totalRawBytes - totalCleanChars) / totalRawBytes;
        double throughputMbPerSec = (totalRawBytes / (1024.0 * 1024.0)) / (Math.max(totalParseMs, 1) / 1000.0);
        long rawEstTokens = totalRawBytes / 4;
        long cleanEstTokens = totalCleanChars / 4;
        long tokensSaved = rawEstTokens - cleanEstTokens;

        System.out.println(darkGray("========================================================================================================================"));
        System.out.printf(" " + boldWhite("SCRAPING COMPLETE:") + darkGray(" Processed ") + boldWhite(String.format("%.2f MB", mbTotal)) + darkGray(" across 20 live nodes in ") + boldWhite(String.format("%,d ms native AVX2 time", totalParseMs)) + darkGray(" (%s)\n"),
                boldWhite(String.format("%.1f GB/s SIMD throughput", throughputMbPerSec / 1024.0)));
        System.out.printf(" " + darkGray("Extracted ") + boldWhite(String.format("%,d clean chars", totalCleanChars)) + darkGray(" (-%.1f%% noise stripped) | Saved ") + boldWhite(String.format("%,d LLM Prompt Tokens", tokensSaved)) + darkGray(" from raw HTML bloat.\n"), overallReduction);
        System.out.printf(" " + darkGray("Harvested ") + boldWhite(String.format("%,d total encyclopedic reference links", totalLinksExtracted)) + darkGray(" in microsecond native bursts.\n"));
        System.out.println(darkGray("========================================================================================================================"));
    }

    private static List<String> sanitizeHeadings(FastScrape scraper, List<String> rawH2s) {
        List<String> results = new ArrayList<>();
        for (String raw : rawH2s) {
            String clean = scraper.extractReadableText(raw.getBytes(StandardCharsets.UTF_8)).trim();
            if (clean.length() > 2 && !clean.equalsIgnoreCase("Contents") && !clean.equalsIgnoreCase("References") && !clean.equalsIgnoreCase("See also") && !clean.equalsIgnoreCase("External links") && !clean.equalsIgnoreCase("Navigation menu")) {
                results.add(clean);
            }
        }
        return results;
    }

    private static String findFirstEncyclopedicParagraph(String text, String title) {
        String[] lines = text.split("\n");
        for (String l : lines) {
            String t = l.trim();
            // Match genuine full prose paragraphs
            if (t.length() > 80 && !t.contains("disambiguation") && !t.startsWith("Jump to") && !t.startsWith("Main menu")
                    && !t.startsWith("Toggle ") && !t.startsWith("For other uses") && !t.startsWith("For the ")
                    && !t.startsWith("{{") && !t.startsWith("Coordinates") && !t.startsWith("Page semi-protected")) {
                return t;
            }
        }
        // Fallback to title line or first substantial line
        for (String l : lines) {
            String t = l.trim();
            if (t.length() > 50 && !t.startsWith("Toggle ") && !t.startsWith("Jump to") && !t.startsWith("Main menu")) {
                return t;
            }
        }
        return "";
    }

    private static String extractPageTitleFromUrl(String url) {
        int idx = url.lastIndexOf('/');
        if (idx != -1 && idx + 1 < url.length()) {
            return url.substring(idx + 1).replace('_', ' ');
        }
        return url;
    }

    private static List<String> filterWikiArticleLinks(List<String> rawHrefs) {
        List<String> links = new ArrayList<>();
        for (String href : rawHrefs) {
            if (href == null || href.isEmpty()) continue;
            if (href.startsWith("/wiki/")) {
                String sub = href.substring(6);
                if (!sub.contains(":") && !sub.contains("#") && !sub.contains("?") && !sub.equals("Main_Page")) {
                    links.add("https://en.wikipedia.org" + href);
                }
            } else if (href.startsWith("https://en.wikipedia.org/wiki/")) {
                String sub = href.substring(30);
                if (!sub.contains(":") && !sub.contains("#") && !sub.contains("?") && !sub.equals("Main_Page")) {
                    links.add(href);
                }
            }
        }
        return links.stream().distinct().toList();
    }

    private static String truncate(String text, int maxLen) {
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }

    private static String darkGray(String text) {
        return FastANSI.fg(240) + text + FastANSI.RESET;
    }

    private static String white(String text) {
        return FastANSI.FG_BRIGHT_WHITE + text + FastANSI.RESET;
    }

    private static String boldWhite(String text) {
        return FastANSI.BOLD + FastANSI.FG_BRIGHT_WHITE + text + FastANSI.RESET;
    }
}
