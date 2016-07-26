package pl.kawowydzienniczek.kawowydzienniczek;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Globals.User;
import pl.kawowydzienniczek.kawowydzienniczek.Services.KawowyDzienniczekService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
@PrepareForTest({KawowyDzienniczekService.class, HttpUrl.class})
@PowerMockIgnore("javax.net.ssl.*")
public class KawowyDzienniczekServiceTest {

    KawowyDzienniczekService kawowyService;

    @Before
    public void Init(){
        kawowyService = new KawowyDzienniczekService();
        kawowyService.setClient(mockClient);
    }

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    OkHttpClient mockClient;

    @Spy
    Request.Builder spyBuilder = spy(Request.Builder.class);

    @Test
    public void postRequestWithParameters_AuthProvided() throws Exception {
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(spyBuilder);
        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);

        kawowyService.postRequestWithParameters("http://google.com", "params", "auth");
        verify(spyBuilder, atMost(1)).addHeader(eq("Authorization"), contains("Token"));
    }

    @Test
    public void postRequestWithParameters_NoAuthProvided() throws Exception {
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(spyBuilder);
        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);

        kawowyService.postRequestWithParameters("http://google.com", "params", null);
        verify(spyBuilder, never()).addHeader(anyString(), anyString());
    }

    @Test
    public void getRequest_AuthProvided() throws Exception{
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(spyBuilder);
        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);

        kawowyService.getRequest("http://google.com", "auth");
        verify(spyBuilder, times(1)).addHeader(eq("Authorization"), contains("Token"));
    }

    @Test
    public void getRequest_NoAuthProvided() throws Exception{
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(spyBuilder);
        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);

        kawowyService.getRequest("http://google.com", null);
        verify(spyBuilder, never()).addHeader(anyString(), anyString());
    }

    @Test
    public void getRequestWIthParameters_AuthProvided() throws Exception{
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(spyBuilder);
        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);

        HashMap<String,String> fakeMap = new HashMap<>();
        kawowyService.getRequestWithParameters("http://google.com", "auth", fakeMap);

        verify(spyBuilder, times(1)).addHeader(eq("Authorization"), contains("Token"));
    }

    @Test
    public void getRequestWithParameters_NoAuthProvided() throws Exception{
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(spyBuilder);
        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);

        HashMap<String,String> fakeMap = new HashMap<>();
        kawowyService.getRequestWithParameters("http://google.com", null, fakeMap);

        verify(spyBuilder, never()).addHeader(anyString(), anyString());
    }

    @Test
    public void getRequestWithParameters_ParametersAddedXTimes() throws Exception{
        HttpUrl.Builder mockHttpBuilder = PowerMockito.mock(HttpUrl.Builder.class,RETURNS_DEEP_STUBS);
        HttpUrl mockTempHttp = PowerMockito.mock(HttpUrl.class);

        PowerMockito.mockStatic(HttpUrl.class);
        PowerMockito.when(HttpUrl.parse(anyString())).thenReturn(mockTempHttp);
        PowerMockito.when(mockTempHttp.newBuilder()).thenReturn(mockHttpBuilder);
        doReturn(mockHttpBuilder).when(mockHttpBuilder).addQueryParameter(anyString(),anyString());

        Request.Builder mockBuilder = spy(Request.Builder.class);
        PowerMockito.whenNew(Request.Builder.class).withAnyArguments().thenReturn(mockBuilder);
        doReturn(mockBuilder).when(mockBuilder).url(anyString());
        doReturn(null).when(mockBuilder).build();

        PowerMockito.when(mockClient.newCall(any(Request.class)).execute()).thenReturn(null);
        HashMap<String,String> fakeMap = new HashMap<>();
        fakeMap.put("one","one");
        fakeMap.put("two","two");
        fakeMap.put("three", "three");

        kawowyService.getRequestWithParameters("http://google.com", "auth", fakeMap);
        verify(mockHttpBuilder).build();
        verify(mockHttpBuilder, times(3)).addQueryParameter(anyString(),anyString());
    }

    @Test
    public void getToken_ValidInput() throws Exception{
        String response = "{\n" +
                "            \"token\": \"something\" \n" +
                "        }";
        JSONObject json = new JSONObject(response);
        JSONObject spyJson = PowerMockito.spy(json);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);

        kawowyService.getToken(response);
        verify(spyJson, times(1)).getString(eq("token"));
        verify(spyJson,never()).optString(eq("non_field_errors"));
    }

    @Test
    public void getToken_InvalidInput() throws Exception{
        String response = "{\n" +
                "            \"detail\": \"something\" \n" +
                "        }";
        JSONObject json = new JSONObject(response);
        JSONObject spyJson = PowerMockito.spy(json);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);

        kawowyService.getToken(response);
        verify(spyJson).optString(eq("non_field_errors"));
        verify(spyJson).getString(eq("token"));
    }

    @Test
    public void isRequestAuthorized_ValidInput() throws Exception{
        String response = "{\n" +
                "            \"tokenos\": \"something\" \n" +
                "        }";
        JSONObject json = new JSONObject(response);
        JSONObject spyJson = PowerMockito.spy(json);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);

        boolean result = kawowyService.isRequestAuthorized(response);
        verify(spyJson, times(1)).has("detail");
        assertTrue(result);
    }

    @Test
    public void isRequestAuthorized_InvalidInput() throws Exception{
        String response = "{\n" +
                "            \"detail\": \"something\" \n" +
                "        }";
        JSONObject json = new JSONObject(response);
        JSONObject spyJson = PowerMockito.spy(json);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);

        boolean result = kawowyService.isRequestAuthorized(response);
        verify(spyJson, times(1)).has("detail");
        assertFalse(result);
    }

    @Test
    public void makeJsonUsernameCredentials_DefaultInput() throws Exception{
        JSONObject spyJson = Mockito.spy(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);
        String result = kawowyService.makeJsonUsernameCredentials("admin", "pass");

        verify(spyJson).put(eq("email"), anyString());
        verify(spyJson).put(eq("password"), anyString());
        assertThat(result, new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                String input = ((String) argument);
                return input.contains("email")
                        && input.contains("password")
                        && input.contains("admin")
                        && input.contains("pass");
            }
        });
    }

    @Test
    public void getProductData_ValidInput() throws Exception{
        JSONObject json = spy(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(json);
        kawowyService.getProductData("{\n" +
                "    \"url\": \"http://kawowydzienniczek.pl/api/products/1/\",\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Kawa Latte\",\n" +
                "    \"description\": \"Z nutą czekolady\",\n" +
                "    \"price\": \"7 zł\",\n" +
                "    \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\"\n" +
                "}");

        verify(json).optString(eq("id"));
        verify(json).optString(eq("name"));
        verify(json).optString(eq("description"));
        verify(json).optString(eq("price"));
        verify(json).optString(eq("img"));

    }

    @Test
    public void isDateWithinRange_NullStartDate() throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date testDate = format.parse("05/27/2015");
        Date startDate = null;
        Date endDate = format.parse("09/28/1992");

        boolean result = kawowyService.isDateWithinRange(startDate,endDate,testDate);
        assertFalse(result);
    }

    @Test
    public void isDateWithinRange_EveryDateOk() throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date testDate = format.parse("05/27/2015");
        Date startDate = format.parse("05/27/1992");
        Date endDate = format.parse("09/28/2016");

        boolean result = kawowyService.isDateWithinRange(startDate,endDate,testDate);
        assertTrue(result);
    }

    @Test
    public void getPromotionDataByStatusReplaceExistingList_ActiveAndAvailableData() throws Exception{
        String response = "{\n" +
                "    \"results\": [\n" +
                "        {\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/user_promotions/3/\",\n" +
                "            \"id\": 3,\n" +
                "            \"user\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/user/2/\",\n" +
                "                \"id\": 2,\n" +
                "                \"username\": \"Antek\",\n" +
                "                \"email\": \"antek@gmail.com\"\n" +
                "            },\n" +
                "            \"place\": \"http://kawowydzienniczek.pl/api/places/2/\",\n" +
                "            \"promotion\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/promotions/3/\",\n" +
                "                \"id\": 3,\n" +
                "                \"name\": \"50%\",\n" +
                "                \"description\": \"Zniżka 50%\",\n" +
                "                \"code\": \"5ac7\",\n" +
                "                \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "                \"status\": \"AV\",\n" +
                "                \"left_number\": \"0\",\n" +
                "                \"start_date\": \"2016-05-22\",\n" +
                "                \"end_date\": \"2017-12-22\"\n" +
                "            },\n" +
                "            \"progress\": \"AV\",\n" +
                "            \"code\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/user_promotions/4/\",\n" +
                "            \"id\": 4,\n" +
                "            \"user\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/user/2/\",\n" +
                "                \"id\": 2,\n" +
                "                \"username\": \"Antek\",\n" +
                "                \"email\": \"antek@gmail.com\"\n" +
                "            },\n" +
                "            \"place\": \"http://kawowydzienniczek.pl/api/places/3/\",\n" +
                "            \"promotion\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/promotions/5/\",\n" +
                "                \"id\": 5,\n" +
                "                \"name\": \"80%\",\n" +
                "                \"description\": \"Zniżka 80%\",\n" +
                "                \"code\": \"5ac725f\",\n" +
                "                \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "                \"status\": \"AV\",\n" +
                "                \"left_number\": \"0\",\n" +
                "                \"start_date\": \"null\",\n" +
                "                \"end_date\": \"null\"\n" +
                "            },\n" +
                "            \"progress\": \"AC\",\n" +
                "            \"code\": null\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        JSONObject json = new JSONObject(response);
        JSONObject spyJson = PowerMockito.spy(json);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);
        JSONArray resultsArray = json.getJSONArray("results");
        JSONArray spyResultsArray = PowerMockito.spy(resultsArray);
        PowerMockito.doReturn(spyResultsArray).when(spyJson).getJSONArray(eq("results"));

        ArgumentCaptor<Integer> integersCapturer = ArgumentCaptor.forClass(Integer.class);

        List<String> statusesWanted = new ArrayList<>();
        statusesWanted.add(GeneralConstants.USER_PROMOTION_PROGRESS_ACTIVE);
        statusesWanted.add(GeneralConstants.USER_PROMOTION_PROGRESS_AVAILABLE);

        kawowyService.getPromotionDataByStatusReplaceExistingList(new ArrayList<KawowyDzienniczekService.PromotionData>(),
                response, statusesWanted);
        verify(spyResultsArray, times(2)).getJSONObject(integersCapturer.capture());
        List<Integer> capturedInts = integersCapturer.getAllValues();
        assertEquals((int)capturedInts.get(0),0);
        assertEquals((int) capturedInts.get(1), 1);
    }

    @Test
    public  void getAvailablePromotionDataReplaceExistingList_AvailableData() throws Exception{
        String response = "{\n" +
                "    \"promotion\": {\n" +
                "        \"url\": \"http://kawowydzienniczek.pl/api/offers/1/\",\n" +
                "        \"id\": \"1\",\n" +
                "        \"promotions\": [\n" +
                "            {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/promotions/1/\",\n" +
                "                \"id\": \"1\",\n" +
                "                \"name\": \"10%\",\n" +
                "                \"description\": \"Zniżka 10%\",\n" +
                "                \"code\": \"5ac5\",\n" +
                "                \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "                \"status\": \"AV\",\n" +
                "                \"left_number\": \"200\",\n" +
                "                \"start_date\": \"null\",\n" +
                "                \"end_date\": \"null\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/promotions/2/\",\n" +
                "                \"id\": \"2\",\n" +
                "                \"name\": \"20%\",\n" +
                "                \"description\": \"Zniżka 20%\",\n" +
                "                \"code\": \"5ac6\",\n" +
                "                \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "                \"status\": \"NAV\",\n" +
                "                \"left_number\": \"null\",\n" +
                "                \"start_date\": \"null\",\n" +
                "                \"end_date\": \"null\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }, \n" +
                "}";

        List<KawowyDzienniczekService.PromotionData> existingData =
                (List<KawowyDzienniczekService.PromotionData>)spy(List.class);
        JSONObject jsonOriginal = new JSONObject(response);
        JSONObject jsonPromotionObject = jsonOriginal.getJSONObject("promotion");
        JSONArray jsonArray = jsonPromotionObject.getJSONArray("promotions");

        JSONObject spyMainJsonObject = PowerMockito.spy(jsonOriginal);
        JSONObject spyPromotionJsonObject = PowerMockito.spy(jsonPromotionObject);
        JSONArray spyPromotionsJsonArray = PowerMockito.spy(jsonArray);

        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyMainJsonObject);
        doReturn(spyPromotionJsonObject).when(spyMainJsonObject).getJSONObject(eq("promotion"));
        doReturn(spyPromotionsJsonArray).when(spyPromotionJsonObject).getJSONArray(eq("promotions"));

        kawowyService.getAvailablePromotionDataReplaceExistingList(existingData, response);
        verify(spyPromotionsJsonArray,times(2)).getJSONObject(anyInt());
        verify(existingData).add(isA(KawowyDzienniczekService.PromotionData.class));
    }

    @Test
    public void getSinglePromotionData_NullStartDate() throws Exception{
        String response = "{\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/promotions/1/\",\n" +
                "            \"id\": \"1\",\n" +
                "            \"name\": \"10%\",\n" +
                "            \"description\": \"Zniżka 10%\",\n" +
                "            \"code\": \"5ac5\",\n" +
                "            \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "            \"status\": \"AV\",\n" +
                "            \"left_number\": \"200\",\n" +
                "            \"start_date\": \"null\",\n" +
                "            \"end_date\": \"null\"\n" +
                "        }";
        KawowyDzienniczekService.PromotionData result  = kawowyService.getSinglePromotionData(response);
        KawowyDzienniczekService.PromotionData expected =
                new KawowyDzienniczekService.PromotionData(
                        "1","10%","Zniżka 10%","5ac5","http://kawowydzienniczek.pl/static/img/photo_1.jpg",
                        "AV","200",null,null);
        assertEquals(expected, result);
    }

    @Test
    public void getOfferData_ValidData() throws Exception{
        String response = " {\n" +
                "    \"products\": [\n" +
                "        {\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/products/1/\",\n" +
                "            \"id\": \"1\",\n" +
                "            \"name\": \"Kawa Latte\",\n" +
                "            \"description\": \"Z nutą czekolady\",\n" +
                "            \"price\": \"7 zł\",\n" +
                "            \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/products/2/\",\n" +
                "            \"id\": \"2\",\n" +
                "            \"name\": \"Herbata\",\n" +
                "            \"description\": \"Z nutą czekolady\",\n" +
                "            \"price\": \"4 zł\",\n" +
                "            \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject original = new JSONObject(response);
        JSONObject spyJson = Mockito.spy(original);
        JSONArray spyArray = Mockito.spy(original.getJSONArray("products"));
        KawowyDzienniczekService.OfferData one = new KawowyDzienniczekService.OfferData(
                "1","Kawa Latte","Z nutą czekolady","7 zł","http://kawowydzienniczek.pl/static/img/photo_1.jpg"
        );
        KawowyDzienniczekService.OfferData two = new KawowyDzienniczekService.OfferData(
                "2","Herbata","Z nutą czekolady","4 zł","http://kawowydzienniczek.pl/static/img/photo_1.jpg"
        );
        List<KawowyDzienniczekService.OfferData> expected = Arrays.asList(one, two);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(spyJson);
        doReturn(spyArray).when(spyJson).getJSONArray(eq("products"));

        List<KawowyDzienniczekService.OfferData> result = kawowyService.getOfferData(response);
        verify(spyArray, times(2)).getJSONObject(anyInt());
        assertEquals(expected,result);
    }

    @Test
    public void getUserData_ValidData() throws Exception{
        String response = "{\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/user_profile/1/\",\n" +
                "            \"id\": \"1\",\n" +
                "            \"user\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/user/1/\",\n" +
                "                \"id\": \"1\",\n" +
                "                \"username\": \"admin\",\n" +
                "                \"email\": \"admin@gmail.com\"\n" +
                "            },\n" +
                "            \"photo\": \"http://kawowydzienniczek.pl/static/img/users/photos/default_avatar.png\"\n" +
                "        }";
        KawowyDzienniczekService.UserData expected = new KawowyDzienniczekService.UserData(
                "http://kawowydzienniczek.pl/api/user_profile/1/",1,
                new User("http://kawowydzienniczek.pl/api/user/1/",1,"admin","admin@gmail.com"),
                "http://kawowydzienniczek.pl/static/img/users/photos/default_avatar.png");

        KawowyDzienniczekService.UserData actual = kawowyService.getUserData(response);
        assertEquals(expected,actual);
    }

    @Test
    public void getCoffeePreviewData_ValidInput() throws Exception{
        String response = "{\n" +
                "            \"url\": \"http://kawowydzienniczek.pl/api/places/1/\",\n" +
                "            \"id\": \"1\",\n" +
                "            \"name\": \"Kocia Kawiarnia\",\n" +
                "            \"type\": \"Cafeteria\",\n" +
                "            \"screen_img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "            \"logo_img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "            \"description\": \"description\",\n" +
                "            \"offer\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/offers/1/\",\n" +
                "                \"id\": \"1\",\n" +
                "                \"products\": [\n" +
                "                    {\n" +
                "                        \"url\": \"http://kawowydzienniczek.pl/api/products/1/\",\n" +
                "                        \"id\": \"1\",\n" +
                "                        \"name\": \"Kawa Latte\",\n" +
                "                        \"description\": \"Z nutą czekolady\",\n" +
                "                        \"price\": \"7 zł\",\n" +
                "                        \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"url\": \"http://kawowydzienniczek.pl/api/products/2/\",\n" +
                "                        \"id\": \"2\",\n" +
                "                        \"name\": \"Herbata\",\n" +
                "                        \"description\": \"Z nutą czekolady\",\n" +
                "                        \"price\": \"4 zł\",\n" +
                "                        \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            \"promotion\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/offers/1/\",\n" +
                "                \"id\": \"1\",\n" +
                "                \"promotions\": [\n" +
                "                    {\n" +
                "                        \"url\": \"http://kawowydzienniczek.pl/api/promotions/1/\",\n" +
                "                        \"id\": \"1\",\n" +
                "                        \"name\": \"10%\",\n" +
                "                        \"description\": \"Zniżka 10%\",\n" +
                "                        \"code\": \"5ac5\",\n" +
                "                        \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "                        \"status\": \"AV\",\n" +
                "                        \"left_number\": \"200\",\n" +
                "                        \"start_date\": \"null\",\n" +
                "                        \"end_date\": \"null\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"url\": \"http://kawowydzienniczek.pl/api/promotions/2/\",\n" +
                "                        \"id\": \"2\",\n" +
                "                        \"name\": \"20%\",\n" +
                "                        \"description\": \"Zniżka 20%\",\n" +
                "                        \"code\": \"5ac6\",\n" +
                "                        \"img\": \"http://kawowydzienniczek.pl/static/img/photo_1.jpg\",\n" +
                "                        \"status\": \"AV\",\n" +
                "                        \"left_number\": \"null\",\n" +
                "                        \"start_date\": \"null\",\n" +
                "                        \"end_date\": \"null\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            \"localization\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/localizations/1/\",\n" +
                "                \"id\": \"1\",\n" +
                "                \"latitude\": \"0.0000\",\n" +
                "                \"longitude\": \"10.0000\",\n" +
                "                \"city\": \"Kraków\",\n" +
                "                \"road\": \"Makowicka\",\n" +
                "                \"road_number\": \"55\"\n" +
                "            },\n" +
                "            \"beacon\": {\n" +
                "                \"url\": \"http://kawowydzienniczek.pl/api/beacons/1/\",\n" +
                "                \"id\": \"1\",\n" +
                "                \"uuid\": \"f7826da6-4fa2-4e98-8024-bc5b71e0893e\",\n" +
                "                \"major\": \"2667\",\n" +
                "                \"minor\": \"62313\",\n" +
                "                \"name\": \"hPUL\",\n" +
                "                \"description\": \"Bialy\"\n" +
                "            }\n" +
                "        }";

        KawowyDzienniczekService.CoffeePreviewData result = kawowyService.getCoffeePreviewData(response);
        assertEquals(result.getLocalization().getLongitude(), "10.0000");
        assertEquals(result.getDesc(),"description");
        assertEquals(result.getOffer().size(),2);
        assertEquals(result.getPromotion().size(),2);
    }
}

