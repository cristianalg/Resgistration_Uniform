package ipt.ipg.app_uniform.HelperUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperUtilities {

    public static boolean isValidEmail(String email) {
        String regexEmail = "([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";

        if (email.matches(regexEmail)) {
            return true;
        }
        return false;
    }

    public static boolean isEmptyOrNull(String param) {
        if (param == null || param.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isString(String data) {

        if (data.matches("\\d+(?:\\.\\d+)?")) {
            return false;
        }

        return true;
    }

    public static boolean isShortPassword(String password) {
        if (password.length() > 5) {
            return false;
        }
        return true;
    }

    public static String getDateTime() {

        return DateFormat.getDateInstance().format(new Date());

    }

    public static String maskCardNumber(String cardNumber) {
        int index = 0;
        String mask = "************####";
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(cardNumber.charAt(index));
                index++;
            } else if (c == '*') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }

        return maskedNumber.toString();
    }

    public static String capitalize(String str) {
        return str.length() == 0 ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String filter(String input) {
        if (!hasSpecialChars(input)) {
            return (input);
        }
        StringBuilder sb = new StringBuilder(input.length());
        char c;

        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();

    }

    private static boolean hasSpecialChars(String input) {

        Pattern regexSpecialChars = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher inputStr = regexSpecialChars.matcher(input);
        boolean hasSpecialChars = inputStr.find();

        if (!hasSpecialChars) {
            return false;
        }

        return true;
    }

    //calculates the size of the uploaded image
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //reduces the size of the image
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(res, 0, res.length, options);

    }

    public static Double calculateTotalFare(double outboundFare, double returnFare, int numTraveller){
        return (outboundFare + returnFare) * numTraveller;
    }

    public static Double calculateTotalFare(double fare, int numTraveller){
        return fare * numTraveller;
    }

    public static boolean compareDate(String departureDate, String returnDate){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(departureDate);
            Date date2 = sdf.parse(returnDate);

            if (date2.before(date1)) {
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

}
