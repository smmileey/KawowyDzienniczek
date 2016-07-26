package pl.kawowydzienniczek.kawowydzienniczek;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralService;
import pl.kawowydzienniczek.kawowydzienniczek.Services.KawowyDzienniczekService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GeneralServiceTest {

    GeneralService service = new GeneralService();

    @Test
    public void testGetFormattedLocalization() {
        KawowyDzienniczekService.LocalizationData one = new KawowyDzienniczekService.LocalizationData(
                "http://kawowydzienniczek.pl/api/localizations/1/",
                "1","0.0000","10.0000","Kraków","Makowicka","55");
        KawowyDzienniczekService.LocalizationData two = new KawowyDzienniczekService.LocalizationData(
                "http://kawowydzienniczek.pl/api/localizations/2/",
                "2","10.0000","0.0000","Kraków","Rynek","15");

        assertEquals("Kraków, Makowicka 55", service.getFormattedLocalization(one));
        assertEquals("Kraków, Rynek 15", service.getFormattedLocalization(two));
    }

    @Test
    public void testCopyArrayListByValue_FailsIfDestinationIsNull() {
        List<Integer> input = Arrays.asList(1,2,3,4,5);
        List<Integer> result = null;
        assertFalse(service.copyArrayListByValue(input, result));
    }

    @Test
    public void testCopyArrayListByValue_DestinationHasDuplicatedValues() {
        List<Integer> input = Arrays.asList(1,2,3,4,5);
        List<Integer> result = new ArrayList<>();
        assertTrue(service.copyArrayListByValue(input, result));
        assertEquals(input,result);
    }

    @Test
    public void testCopyArrayListByValue_DestinationHasDifferentRefference() {
        List<Integer> input = Arrays.asList(1,2,3,4,5);
        List<Integer> result = new ArrayList<>();
        assertTrue(service.copyArrayListByValue(input,result));
        assertFalse(input == result);
    }
}