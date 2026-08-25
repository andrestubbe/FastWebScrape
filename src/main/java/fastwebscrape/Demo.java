package fastwebscrape;

import fastansi.FastANSI;
import fastwebspider.FastWebSpider;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * FastWebScrape — High-Speed Native AVX2 HTML Scraping & CleanText Hero Demo.
 * Powered by FastWebSpider (Native WinHTTP Session) for multi-node web ingestion and AVX2 for zero-copy text mining.
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
        "https://en.wikipedia.org/wiki/Parallel_computing",
        "https://en.wikipedia.org/wiki/Central_processing_unit",
        "https://en.wikipedia.org/wiki/Microprocessor",
        "https://en.wikipedia.org/wiki/Instruction_set_architecture",
        "https://en.wikipedia.org/wiki/X86",
        "https://en.wikipedia.org/wiki/X86-64",
        "https://en.wikipedia.org/wiki/AArch64",
        "https://en.wikipedia.org/wiki/MIPS_architecture",
        "https://en.wikipedia.org/wiki/PowerPC",
        "https://en.wikipedia.org/wiki/SPARC",
        "https://en.wikipedia.org/wiki/Supercomputer",
        "https://en.wikipedia.org/wiki/High-performance_computing",
        "https://en.wikipedia.org/wiki/Multithreading_(computer_architecture)",
        "https://en.wikipedia.org/wiki/Hyper-threading",
        "https://en.wikipedia.org/wiki/Multi-core_processor",
        "https://en.wikipedia.org/wiki/Cache_(computing)",
        "https://en.wikipedia.org/wiki/CPU_cache",
        "https://en.wikipedia.org/wiki/Branch_predictor",
        "https://en.wikipedia.org/wiki/Out-of-order_execution",
        "https://en.wikipedia.org/wiki/Instruction_pipelining",
        "https://en.wikipedia.org/wiki/Superscalar_processor",
        "https://en.wikipedia.org/wiki/Very_long_instruction_word",
        "https://en.wikipedia.org/wiki/Register_file",
        "https://en.wikipedia.org/wiki/Memory_management_unit",
        "https://en.wikipedia.org/wiki/Translation_lookaside_buffer",
        "https://en.wikipedia.org/wiki/Direct_memory_access",
        "https://en.wikipedia.org/wiki/Virtual_memory",
        "https://en.wikipedia.org/wiki/Page_table",
        "https://en.wikipedia.org/wiki/Random-access_memory",
        "https://en.wikipedia.org/wiki/DDR4_SDRAM",
        "https://en.wikipedia.org/wiki/DDR5_SDRAM",
        "https://en.wikipedia.org/wiki/Non-volatile_memory",
        "https://en.wikipedia.org/wiki/Solid-state_drive",
        "https://en.wikipedia.org/wiki/NVM_Express",
        "https://en.wikipedia.org/wiki/PCI_Express",
        "https://en.wikipedia.org/wiki/Motherboard",
        "https://en.wikipedia.org/wiki/Operating_system",
        "https://en.wikipedia.org/wiki/Linux_kernel",
        "https://en.wikipedia.org/wiki/Microsoft_Windows",
        "https://en.wikipedia.org/wiki/MacOS",
        "https://en.wikipedia.org/wiki/FreeBSD",
        "https://en.wikipedia.org/wiki/POSIX",
        "https://en.wikipedia.org/wiki/System_call",
        "https://en.wikipedia.org/wiki/Kernel_(operating_system)",
        "https://en.wikipedia.org/wiki/Microkernel",
        "https://en.wikipedia.org/wiki/Monolithic_kernel",
        "https://en.wikipedia.org/wiki/Process_(computing)",
        "https://en.wikipedia.org/wiki/Thread_(computing)",
        "https://en.wikipedia.org/wiki/Fiber_(computer_science)",
        "https://en.wikipedia.org/wiki/Coroutine",
        "https://en.wikipedia.org/wiki/Asynchronous_I/O",
        "https://en.wikipedia.org/wiki/Epoll",
        "https://en.wikipedia.org/wiki/Kqueue",
        "https://en.wikipedia.org/wiki/Input/output_completion_port",
        "https://en.wikipedia.org/wiki/Memory-mapped_file",
        "https://en.wikipedia.org/wiki/Shared_memory",
        "https://en.wikipedia.org/wiki/Inter-process_communication",
        "https://en.wikipedia.org/wiki/Pipeline_(Unix)",
        "https://en.wikipedia.org/wiki/Unix_domain_socket",
        "https://en.wikipedia.org/wiki/Network_socket",
        "https://en.wikipedia.org/wiki/Transmission_Control_Protocol",
        "https://en.wikipedia.org/wiki/User_Datagram_Protocol",
        "https://en.wikipedia.org/wiki/Internet_Protocol",
        "https://en.wikipedia.org/wiki/IPv4",
        "https://en.wikipedia.org/wiki/IPv6",
        "https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol",
        "https://en.wikipedia.org/wiki/HTTP/2",
        "https://en.wikipedia.org/wiki/HTTP/3",
        "https://en.wikipedia.org/wiki/Transport_Layer_Security",
        "https://en.wikipedia.org/wiki/Domain_Name_System",
        "https://en.wikipedia.org/wiki/URL",
        "https://en.wikipedia.org/wiki/HTML",
        "https://en.wikipedia.org/wiki/HTML5",
        "https://en.wikipedia.org/wiki/CSS",
        "https://en.wikipedia.org/wiki/JavaScript",
        "https://en.wikipedia.org/wiki/WebAssembly",
        "https://en.wikipedia.org/wiki/JSON",
        "https://en.wikipedia.org/wiki/XML",
        "https://en.wikipedia.org/wiki/UTF-8",
        "https://en.wikipedia.org/wiki/ASCII",
        "https://en.wikipedia.org/wiki/Garbage_collection_(computer_science)"
    );

    public static void main(String[] args) throws Exception {
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println(" " + boldWhite("FastWebScrape") + darkGray(" — Real-Time Native AVX2 HTML Scraping & CleanText Pipeline"));
        System.out.println(darkGray(" INGESTION: FastWebSpider WinHTTP Native Session  |  PARSER: AVX2 Zero-Copy Tag Stripper & Link Harvester"));
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println();

        FastWebScrape scraper = FastWebScrape.open();
        FastWebSpider spider = FastWebSpider.open();

        // ── Phase 0: Target Ingestion Queue Preview ─────────────────────────
        System.out.println(darkGray("[Target Queue]") + " " + boldWhite("Loaded " + TARGET_PAGES.size() + " Hardware, Architecture & Systems Nodes:"));
        for (int i = 0; i < TARGET_PAGES.size(); i++) {
            boolean isLast = (i == TARGET_PAGES.size() - 1);
            String branch = isLast ? "└──" : "├──";
            System.out.printf("  %s %s %s\n", darkGray(branch), boldWhite(String.format("[%03d]", i + 1)), white(TARGET_PAGES.get(i)));
        }
        System.out.println();

        // ── Phase 1: High-Speed WinHTTP Multi-Node Ingestion ─────────────────
        System.out.println(darkGray("[Phase 1]") + " " + boldWhite("FastWebSpider Native WinHTTP Ingestion") + darkGray(" (Downloading " + TARGET_PAGES.size() + " live Wikipedia nodes via Virtual Threads)"));

        long fetchT0 = System.currentTimeMillis();
        List<CompletableFuture<FastWebSpider.SpiderResponse>> futures = new ArrayList<>();
        for (String url : TARGET_PAGES) {
            futures.add(spider.fetchAsync(url));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long fetchDuration = System.currentTimeMillis() - fetchT0;

        List<byte[]> payloads = new ArrayList<>();
        long totalRawBytes = 0;
        for (CompletableFuture<FastWebSpider.SpiderResponse> f : futures) {
            byte[] body = f.join().rawBody();
            payloads.add(body);
            totalRawBytes += body.length;
        }

        double mbTotal = totalRawBytes / (1024.0 * 1024.0);
        double mbPerSec = mbTotal / (Math.max(fetchDuration, 1) / 1000.0);

        System.out.printf("  %s %s across %s in %s (%s)\n\n",
                darkGray("└── Ingested"),
                boldWhite(String.format("%.2f MB raw HTML", mbTotal)),
                boldWhite(TARGET_PAGES.size() + " live nodes"),
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

            boolean isLastNode = (i == TARGET_PAGES.size() - 1);
            String nodeBranch = isLastNode ? "└──" : "├──";
            String subIndent = isLastNode ? "     " : "  │  ";

            System.out.printf("  %s %s %-48s %s %s %s %s\n",
                    darkGray(nodeBranch),
                    boldWhite(String.format("[%03d]", i + 1)),
                    white(shortUrl),
                    darkGray(String.format("| %,6d KB HTML", html.length / 1024)),
                    boldWhite(String.format("| %,6d µs", parseUs)),
                    darkGray(String.format("| %,5d links", validLinks.size())),
                    darkGray(String.format("| -%.1f%% noise", reductionPct)));

            // 1. Title line
            System.out.printf("%s  ├── %s %s\n", subIndent, darkGray("Title:   "), boldWhite(truncate(cleanH1, 70)));

            // 2. Sub-headings (H2) overview
            List<String> cleanH2List = sanitizeHeadings(scraper, rawH2s);
            if (!cleanH2List.isEmpty()) {
                String h2Joined = String.join(" • ", cleanH2List.subList(0, Math.min(cleanH2List.size(), 4)));
                System.out.printf("%s  ├── %s %s\n", subIndent, darkGray("Sections:"), darkGray(truncate(h2Joined, 85)));
            }

            // 3. Extract genuine encyclopedic paragraph for LLM
            String summarySnippet = findFirstEncyclopedicParagraph(cleanText, cleanH1);
            if (!summarySnippet.isEmpty()) {
                System.out.printf("%s  ├── %s %s\n", subIndent, darkGray("Synopsis:"), white(truncate(summarySnippet, 95)));
            }

            // 4. Stream 3 clean article links
            int previewCount = Math.min(validLinks.size(), 3);
            for (int p = 0; p < previewCount; p++) {
                boolean isLast = (p == previewCount - 1);
                String lk = truncate(validLinks.get(p), 65);
                System.out.printf("%s  %s %s %s\n",
                        subIndent,
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
                System.out.printf("  └── %s\n\n", white(truncate(bodyPara, 114)));
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
        System.out.printf(" " + boldWhite("SCRAPING COMPLETE:") + darkGray(" Processed ") + boldWhite(String.format("%.2f MB", mbTotal)) + darkGray(" across " + TARGET_PAGES.size() + " live nodes in ") + boldWhite(String.format("%,d ms native AVX2 time", totalParseMs)) + darkGray(" (%s)\n"),
                boldWhite(String.format("%.1f GB/s SIMD throughput", throughputMbPerSec / 1024.0)));
        System.out.printf(" " + darkGray("Extracted ") + boldWhite(String.format("%,d clean chars", totalCleanChars)) + darkGray(" (-%.1f%% noise stripped) | Saved ") + boldWhite(String.format("%,d LLM Prompt Tokens", tokensSaved)) + darkGray(" from raw HTML bloat.\n"), overallReduction);
        System.out.printf(" " + darkGray("Harvested ") + boldWhite(String.format("%,d total encyclopedic reference links", totalLinksExtracted)) + darkGray(" in microsecond native bursts.\n"));
        System.out.println(darkGray("========================================================================================================================"));
    }

    private static List<String> sanitizeHeadings(FastWebScrape scraper, List<String> rawH2s) {
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
                    && !t.startsWith("{{") && !t.startsWith("Coordinates") && !t.startsWith("Page semi-protected")
                    && !t.startsWith("For an expansion card") && !t.startsWith("This article")) {
                return t;
            }
        }
        // Fallback to substantial line
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
