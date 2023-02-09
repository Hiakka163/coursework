import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        // создаём конфиг
        LinksSuggester linksSuggester = new LinksSuggester(new File("data/config"));
        // перебираем пдфки в data/pdfs
        var dir = new File("data/pdfs");
        if (dir.listFiles() != null) {
            final String path = "data/converted/";
            final String newName = "Converted ";
            for (var fileIn : Objects.requireNonNull(dir.listFiles())) {
                // для каждой пдфки создаём новую в data/converted
                File fileOut = new File(path + newName + fileIn.getName());
                var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter(fileOut));

                // перебираем страницы pdf
                for (int i = 1; i < doc.getNumberOfPages() + 1; i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    List<Suggest> suggest = linksSuggester.suggest(text).stream().distinct().collect(Collectors.toList());
                    for (Suggest s : suggest) {
                        linksSuggester.getSuggestList().remove(s);
                    }
                    // если в странице есть неиспользованные ключевые слова, создаём новую страницу за ней
                    if (!suggest.isEmpty()) {
                        var newPage = doc.addNewPage(i + 1);
                        var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
                        Canvas canvas = new Canvas(newPage, rect);
                        Paragraph paragraph = new Paragraph("Suggestions:\n");
                        paragraph.setFontSize(25);
                        // вставляем туда рекомендуемые ссылки из конфига
                        suggest.forEach((s) -> {
                            PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
                            PdfAction action = PdfAction.createURI(s.getUrl());
                            annotation.setAction(action);
                            Link link = new Link(s.getTitle(), annotation);
                            paragraph.add(link.setUnderline());
                            paragraph.add("\n");
                        });
                        canvas.add(paragraph);
                        i++;
                    }
                }
                doc.close();
            }
        }
    }
}
                 