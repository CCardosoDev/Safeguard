package api;

import com.es.georeferencingservice.CoordinateID;
import com.es.georeferencingservice.Georeferencing;
import com.es.georeferencingservice.GeoreferencingAuthenticationException_Exception;
import com.es.georeferencingservice.GeoreferencingSQLException_Exception;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.ByteBuffer;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Authentication;

/**
 *
 * @author claudia
 */
public class CreatePDFProfile extends HttpServlet {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/safeguardtracking.no-ip.org_8080/NotificationsService/NotificationsServiceImp.wsdl")
    private NotificationsServiceImp_Service service_1;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/192.168.8.217_7706/GeoreferencingService/Georeferencing.wsdl")
    private Georeferencing service;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.BLUE);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        }
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String token = request.getParameter("token");
        if (token == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "token can't be null or empty");
            return;
        }
        int profileID;
        try {
            profileID = Integer.parseInt(request.getParameter("profileID"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "profileID has to be a number > 0");
            return;
        }
        String initDate = request.getParameter("initDate");
        if (initDate == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "initDate has to be a date with format YYYY-MM-DD");
            return;
        }
        initDate += " 00:00:00";
        String endDate = request.getParameter("endDate");
        if (endDate == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "endDate has to be a date with format YYYY-MM-DD");
            return;
        }
        endDate += " 00:00:00";

        String email = request.getParameter("destinationEmail");

        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        if (subject == null) {
            subject = "";
        }
        if (message == null) {
            message = "";
        }

        try {
            Document document = new Document();
            ByteBuffer byteBuffer = new ByteBuffer();
            PdfWriter.getInstance(document, byteBuffer);
            document.open();
            document.addTitle("Safeguard Profile Description");
            boolean ok = this.getProfile(response, document, token, profileID, initDate, endDate);
            if (ok) {
                document.close();
                if (email != null) {
                    System.out.println("Sending email");
                    this.sendEmail("safeguardapp@gmail.com", "appsafeguard", email, subject, message, byteBuffer.getBuffer(), (UUID.randomUUID().toString().replaceAll("-", "") + ".pdf"));
                    System.out.println("SentEmail");
                }
                //response.getOutputStream().write(byteBuffer.getBuffer());
            }
        } catch (CouldNotSendEmailException_Exception ex) {
            System.out.println(ex.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (DocumentException ex) {
            System.out.println("Documment exception");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document exception");
        } catch (IOException ex) {
            System.out.println("IOException");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "IOException");
        }

    }

    protected boolean getProfile(HttpServletResponse response, Document document, String token, int profileID, String initDate, String endDate) throws IOException {
        URL url;
        HttpsURLConnection con;
        try {
            url = new URL("https://127.0.0.1:8181/PofilesService/GetProfiles?token=" + token);
            con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(new NullHostnameVerifier());
            con.setUseCaches(false); // ignore caches
            con.setDoInput(true); // defaults to true, but doesn't hurt
            con.connect(); // connect to the server
        } catch (MalformedURLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Mal formed URL Exception");
            return false;
        }


        BufferedReader bufferedReader;
        String sentJson = new String();
        String line;
        if (con.getResponseCode() == 200) {
            InputStream is = con.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems finding profile");
            return false;
        }

        while ((line = bufferedReader.readLine()) != null) {
            sentJson += line;
        }
        JSONArray JSONProfiles;
        try {
            JSONProfiles = new JSONArray(sentJson);
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error reading profile - init");
            return false;
        }

        if (JSONProfiles.length() == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The profile does not exists");
            return false;
        }

        for (int i = 0; i < JSONProfiles.length(); i++) {
            JSONObject JSONProfileID;
            try {
                JSONProfileID = JSONProfiles.getJSONObject(i);
            } catch (JSONException ex) {
                continue;
            }
            int localProfileID;
            try {
                localProfileID = JSONProfileID.getInt("profileID");
            } catch (JSONException ex) {
                continue;
            }

            if (localProfileID == profileID) {
                Anchor anchor = new Anchor("Safeguard Profile Description", catFont);
                anchor.setName("Safeguard Profile Description");
                Paragraph chapterParag = new Paragraph(anchor);
                chapterParag.setAlignment(Element.ALIGN_CENTER);
                Chapter chapter = new Chapter(chapterParag, 0);
                chapter.setNumberDepth(0);
                try {
                    String imageUrl = JSONProfileID.getString("photoLink");
                    Image image = Image.getInstance(new URL(imageUrl));
                    image.scaleToFit(100, 100);
                    chapter.add(image);
                } catch (JSONException | BadElementException | MalformedURLException ex) {
                }

                try {
                    String value = JSONProfileID.getString("fullName");
                    Paragraph subPara = new Paragraph("Full name: ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    subCatPart.add(new Paragraph("    " + value));
                } catch (JSONException ex) {
                }

                try {
                    String value = JSONProfileID.getString("gender");
                    Paragraph subPara = new Paragraph("Gender: ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (value.equalsIgnoreCase("f")) {
                        subCatPart.add(new Paragraph("    " + "female"));
                    }
                    if (value.equalsIgnoreCase("m")) {
                        subCatPart.add(new Paragraph("    " + "male"));
                    }
                } catch (JSONException ex) {
                }

                try {
                    JSONArray values = JSONProfileID.getJSONArray("addresses");
                    Paragraph subPara = new Paragraph("Address(s): ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (values.length() == 0) {
                        subCatPart.add(new Paragraph("    Nothing to report."));
                    }
                    for (int j = 0; j < values.length(); j++) {
                        Section subSection = subCatPart.addSection(new Paragraph("    Addresse " + (j + 1) + ":"), 0);
                        JSONObject addrress = values.getJSONObject(j);
                        subSection.add(new Paragraph("        Street address: " + addrress.getString("streetAddress")));
                        subSection.add(new Paragraph("        Locality: " + addrress.getString("locality")));
                        subSection.add(new Paragraph("        Postal Code: " + addrress.getString("postCode")));
                        subSection.add(new Paragraph("        Country: " + addrress.getString("countryName")));
                    }
                } catch (JSONException ex) {
                }

                try {
                    JSONArray values = JSONProfileID.getJSONArray("phones");
                    Paragraph subPara = new Paragraph("Phone number(s): ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (values.length() == 0) {
                        subCatPart.add(new Paragraph("    Nothing to report."));
                    }
                    for (int j = 0; j < values.length(); j++) {
                        String value = values.getString(j);
                        subCatPart.add(new Paragraph("    " + value));
                    }
                } catch (JSONException ex) {
                }

                try {
                    JSONArray values = JSONProfileID.getJSONArray("emails");
                    Paragraph subPara = new Paragraph("Email(s): ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (values.length() == 0) {
                        subCatPart.add(new Paragraph("    Nothing to report."));
                    }
                    for (int j = 0; j < values.length(); j++) {
                        String value = values.getString(j);
                        subCatPart.add(new Paragraph("    " + value));
                    }
                } catch (JSONException ex) {
                }

                try {
                    JSONArray values = JSONProfileID.getJSONArray("personalPages");
                    Paragraph subPara = new Paragraph("Personal Page(s): ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (values.length() == 0) {
                        subCatPart.add(new Paragraph("    Nothing to report."));
                    }
                    for (int j = 0; j < values.length(); j++) {
                        String value = values.getString(j);
                        subCatPart.add(new Paragraph("    " + value));
                    }
                } catch (JSONException ex) {
                }

                try {
                    String value = JSONProfileID.getString("bloodType");
                    Paragraph subPara = new Paragraph("Blood Type: ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    subCatPart.add(new Paragraph("    " + value));
                } catch (JSONException ex) {
                }

                try {
                    JSONArray values = JSONProfileID.getJSONArray("diseases");
                    Paragraph subPara = new Paragraph("Disease(s): ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (values.length() == 0) {
                        subCatPart.add(new Paragraph("    Nothing to report."));
                    }
                    for (int j = 0; j < values.length(); j++) {
                        String value = values.getString(j);
                        subCatPart.add(new Paragraph("    " + value));
                    }
                } catch (JSONException ex) {
                }

                try {
                    JSONArray values = JSONProfileID.getJSONArray("allergies");
                    Paragraph subPara = new Paragraph("Allergie(s): ", subFont);
                    Section subCatPart = chapter.addSection(subPara, 0);
                    if (values.length() == 0) {
                        subCatPart.add(new Paragraph("    Nothing to report."));
                    }
                    for (int j = 0; j < values.length(); j++) {
                        String value = values.getString(j);
                        subCatPart.add(new Paragraph("    " + value));
                    }
                } catch (JSONException ex) {
                }
                String georefToken = Authentication.getServerAccess(response, token, "georeferencing");
                if (georefToken != null) {
                    try {
                        Paragraph subPara = new Paragraph("Positions History: ", subFont);
                        Section subCatPart = chapter.addSection(subPara, 0);
                        List<CoordinateID> coordinates = this.getCoordinateHistory(georefToken, (new Integer(profileID)).toString(), initDate, endDate);
                        if (coordinates.isEmpty()) {
                            subCatPart.add(new Paragraph("    Nothing to report."));
                        } else {
                            PdfPTable table = new PdfPTable(3);
                            table.setSpacingBefore(5f);
                            try {
                                table.setWidths(new int[]{100, 50});
                            } catch (DocumentException ex) {
                            }
                            PdfPCell c1 = new PdfPCell(new Phrase("Date"));
                            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(c1);
                            c1 = new PdfPCell(new Phrase("Coordinate"));
                            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(c1);
                            c1 = new PdfPCell(new Phrase("Adress"));
                            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(c1);
                            table.setHeaderRows(1);
                            for (CoordinateID coordinate : coordinates) {
                                table.addCell(coordinate.getModified());
                                Chunk chunk = new Chunk(coordinate.getCoordinate().getLatitude() + " , " + coordinate.getCoordinate().getLongitude());
                                chunk.setAnchor("https://www.google.com/maps/preview#!q=" + coordinate.getCoordinate().getLatitude() + "%2C" + coordinate.getCoordinate().getLongitude());
                                chunk.setUnderline(0.4f, -1f);
                                chunk.setFont(blueFont);
                                PdfPCell pCell = new PdfPCell();
                                pCell.addElement(chunk);
                                table.addCell(pCell);
                                URL urlnominatim = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lat=" + coordinate.getCoordinate().getLatitude() + "&lon=" + coordinate.getCoordinate().getLongitude() + "&zoom=18&addressdetails=1");
                                URLConnection urlCon = urlnominatim.openConnection();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                                String line2;
                                String sentJsonNominatim = "";
                                while ((line2 = reader.readLine()) != null) {
                                    sentJsonNominatim += line2;
                                }
                                try {
                                    JSONObject nominatimJSON = new JSONObject(sentJsonNominatim);
                                    String address = nominatimJSON.getString("display_name");
                                    table.addCell(address);
                                } catch (Exception ex) {
                                    table.addCell("");
                                }
                            }
                            subCatPart.add(table);
                        }

                    } catch (GeoreferencingSQLException_Exception | GeoreferencingAuthenticationException_Exception ex) {
                    }
                }
                try {
                    document.add(chapter);

                } catch (DocumentException ex) {
                    ex.printStackTrace();
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not create document");
                    return false;
                }


                break;

            }

        }

        return true;

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private java.util.List<com.es.georeferencingservice.CoordinateID> getCoordinateHistory(java.lang.String token, java.lang.String childUserID, java.lang.String startTime, java.lang.String endTime) throws GeoreferencingSQLException_Exception, GeoreferencingAuthenticationException_Exception {
        com.es.georeferencingservice.GeoreferencingImpl port = service.getGeoreferencingImplPort();
        return port.getCoordinateHistory(token, childUserID, startTime, endTime);
    }


    private void sendEmail(java.lang.String sourceEmail, java.lang.String sourcePassword, java.lang.String destinationEmail, java.lang.String subject, java.lang.String body, byte[] attachment, java.lang.String attachmentName) throws CouldNotSendEmailException_Exception {
        api.NotificationsServiceImp port = service_1.getNotificationsServiceImpPort();
        port.sendEmail(sourceEmail, sourcePassword, destinationEmail, subject, body, attachment, attachmentName);
    }
}
