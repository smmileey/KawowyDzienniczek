package pl.kawowydzienniczek.kawowydzienniczek.Services;


import java.util.List;

public class GeneralService {

    public String getFormattedLocalization(KawowyDzienniczekService.LocalizationData localizationData){
            return localizationData.getCity()+", "+localizationData.getRoad()+" "+
                    localizationData.getRoad_number();
    }

    public <T> Boolean copyArrayListByValue(List<T> source, List<T> destination){
        if(destination == null)
            return false;

        destination.clear();
        for (T item:source) {
                destination.add(item);
        }
        return true;
    }
}
