package fastscrape;

import fastansi.FastANSI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * FastScrape — High-Speed Native AVX2 HTML Scraping & Text Extraction Hero Demo.
 * Demonstrates real-time SIMD tag stripping, link harvesting, and JSON-LD/Metadata parsing across live architecture nodes.
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
        "https://en.wikipedia.org/wiki/OpenCL"
    );

    public static void main(String[] args) throws Exception {
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println(" " + boldWhite("FastScrape") + darkGray(" — Real-Time Native AVX2 HTML Scraping & CleanText Engine"));
        System.out.println(darkGray(" MISSION: Extract readable text, hyperlinks, headings, and JSON-LD from live HTML payloads in microsecond bursts"));
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println();

        FastScrape scraper = FastScrape.open();

        // ── Phase 1: Live Concurrent Fetch ──────────────────────────────────
        System.out.println(darkGray("[Phase 1]") + " " + boldWhite("Live Concurrent Network Ingestion") + darkGray(" (Downloading 10 heavy Wikipedia articles)"));
        HttpClient http = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();

        long fetchT0 = System.currentTimeMillis();
        List<CompletableFuture<HttpResponse<byte[]>>> futures = new ArrayList<>();
        for (String url : TARGET_PAGES) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "FastScrape-HeroDemo/0.1.1 (Java 17+; AVX2)")
                    .build();
            futures.add(http.sendAsync(req, HttpResponse.BodyHandlers.ofByteArray()));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long fetchDuration = System.currentTimeMillis() - fetchT0;

        List<byte[]> payloads = new ArrayList<>();
        long totalRawBytes = 0;
        for (CompletableFuture<HttpResponse<byte[]>> f : futures) {
            byte[] body = f.join().body();
            payloads.add(body);
            totalRawBytes += body.length;
        }

        System.out.printf("  %s %s across %s in %s (%s)\n\n",
                darkGray("└── Ingested"),
                boldWhite(String.format("%.2f MB raw HTML", totalRawBytes / (1024.0 * 1024.0))),
                boldWhite("10 live articles"),
                boldWhite(String.format("%,d ms", fetchDuration)),
                darkGray(String.format("%.1f MB/s", (totalRawBytes / (1024.0 * 1024.0)) / (fetchDuration / 1000.0))));

        // ── Phase 2: Microsecond Real-Time SIMD Parsing Stream ──────────────
        System.out.println(darkGray("[Phase 2]") + " " + boldWhite("Zero-Allocation AVX2 Extraction Stream") + darkGray(" (Stripping tags, parsing titles, links & LLM text)"));
        System.out.println();

        long totalParseNanos = 0;
        long totalCleanChars = 0;
        int totalLinksExtracted = 0;

        for (int i = 0; i < TARGET_PAGES.size(); i++) {
            String url = TARGET_PAGES.get(i);
            byte[] html = payloads.get(i);
            String shortUrl = truncate(url, 48);

            long parseT0 = System.nanoTime();
            String cleanText = scraper.extractReadableText(html);
            List<String> links = scraper.extractLinks(html);
            List<String> headings = scraper.extractByTag(html, "h1");
            long parseUs = (System.nanoTime() - parseT0) / 1000;

            totalParseNanos += (System.nanoTime() - parseT0);
            totalCleanChars += cleanText.length();
            totalLinksExtracted += links.size();

            double reductionPct = 100.0 * (html.length - cleanText.length()) / html.length;
            String h1 = headings.isEmpty() ? "Document" : headings.get(0).trim();

            System.out.printf("  ├── %s %-48s %s %s %s %s\n",
                    boldWhite(String.format("[%02d]", i + 1)),
                    white(shortUrl),
                    darkGray(String.format("| %,6d KB HTML", html.length / 1024)),
                    boldWhite(String.format("| %,6d µs", parseUs)),
                    darkGray(String.format("| %,5d links", links.size())),
                    darkGray(String.format("| -%.1f%% noise", reductionPct)));

            // Show 3 preview links + heading info per page
            System.out.printf("  │    ├── %s %s\n", darkGray("Title:"), boldWhite(truncate(h1, 60)));
            int previewCount = Math.min(links.size(), 3);
            for (int p = 0; p < previewCount; p++) {
                boolean isLast = (p == previewCount - 1);
                String lk = truncate(links.get(p), 62);
                System.out.printf("  │    %s %s %s\n",
                        darkGray(isLast ? "└──" : "├──"),
                        darkGray(String.format("[LINK %02d]", p + 1)),
                        darkGray(lk));
            }
        }
        System.out.println();

        // ── Phase 3: High-Fidelity LLM Context Preview ──────────────────────
        System.out.println(darkGray("[Phase 3]") + " " + boldWhite("LLM Plaintext Compression Sample") + darkGray(" (Showing first 4 lines of extracted context from root node)"));
        System.out.println();

        byte[] firstHtml = payloads.get(0);
        String sampleClean = scraper.extractReadableText(firstHtml).trim();
        String[] lines = sampleClean.split("\n");
        int shownLines = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                System.out.printf("  │  %s\n", white(truncate(trimmed, 110)));
                shownLines++;
                if (shownLines >= 5) break;
            }
        }
        System.out.println();

        // ── Performance Summary Card ─────────────────────────────────────────
        long totalParseMs = totalParseNanos / 1_000_000;
        double overallReduction = 100.0 * (totalRawBytes - totalCleanChars) / totalRawBytes;
        double throughputMbPerSec = (totalRawBytes / (1024.0 * 1024.0)) / (Math.max(totalParseMs, 1) / 1000.0);

        System.out.println(darkGray("========================================================================================================================"));
        System.out.printf(" " + boldWhite("SCRAPING COMPLETE:") + darkGray(" Processed ") + boldWhite(String.format("%.2f MB", totalRawBytes / (1024.0 * 1024.0))) + darkGray(" of live HTML in ") + boldWhite(String.format("%,d ms native AVX2 time", totalParseMs)) + darkGray(" (%s)\n"),
                boldWhite(String.format("%.1f GB/s SIMD throughput", throughputMbPerSec / 1024.0)));
        System.out.printf(" " + darkGray("Extracted ") + boldWhite(String.format("%,d clean chars", totalCleanChars)) + darkGray(" (-%.1f%% noise stripped) and harvested ") + boldWhite(String.format("%,d total hyperlinks", totalLinksExtracted)) + darkGray(" for LLMs.\n"), overallReduction);
        System.out.println(darkGray("========================================================================================================================"));
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
