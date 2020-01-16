import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class Solution {
    public static void main(String[] args) throws IOException, ParseException {
        String file = args.length != 0 ? args[0] : "D:/test.json";
        String res = fight(jsonReader(file), "Владивосток", "Тель-Авив", 90.00);
        //Вывод ответа в консоль
        System.out.println(res);
    }

    /**
     * Json-mapper
     *
     * @param file json-фойл
     * @return Объект представленный в фале file
     * @throws IOException Искючение при отображении json в объект
     */
    private static TicketsList jsonReader(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(file), TicketsList.class);
    }

    /**
     * Расчет среднего значения полетов между указанными городами и процентиля для procentil
     *
     * @param ticketsList     Объект типа TicketsList содержащий список билетов
     * @param originName      Город отправления
     * @param destinationName Город назначения
     * @param procentil       Значение процента для поиска процентиля
     * @return Результат в текстовой форме
     * @throws ParseException Исключение возникает при передаче некорректной даты отправления\назначения
     */
    public static String fight(TicketsList ticketsList, String originName, String destinationName, Double procentil) throws ParseException {
        StringBuilder result = new StringBuilder();
        List<Ticket> list = ticketsList.getTickets();
        long sum = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy'T'HH:mm");
        long travelTime = 0;
        List<Long> travelTimes = new ArrayList<>();
        Date start;
        Date finish;
        for (Ticket ticket : list) {
            if (ticket.getOrigin_name().equals(originName) && ticket.getDestination_name().equals(destinationName)) {
                start = dateFormat.parse(ticket.getDeparture_date() + "T" + ticket.getDeparture_time());
                finish = dateFormat.parse(ticket.getArrival_date() + "T" + ticket.getArrival_time());
                travelTime = finish.getTime() - start.getTime();
                sum += travelTime;
            }
            travelTimes.add(travelTime);
        }

        sum /= list.size();
        //Получение целого количества часов из миллисекунд
        int hours = (int) sum / 1000 / 3600;
        //Получение целого количества минут из миллисекунд
        int minutes = (int) sum / 1000 / 60 % 60;
        result.append(String.format("Среднее время полета между городами %s и %s составляет:%n%d ч. и %d мин.%n",
                originName, destinationName, hours, minutes));

        travelTime = getProcentil(travelTimes, procentil);
        hours = (int) travelTime / 1000 / 3600;
        minutes = (int) travelTime / 1000 / 60 % 60;
        result.append(String.format("%.2f-й процентиль времени полета между городами %s и %s составляет:%n%d ч. и %d мин.%n",
                procentil, originName, destinationName, hours, minutes));
        return result.toString();
    }

    /**
     * Возвращает первое первое значение из упорядоченного списка list после процентиля
     *
     * @param list список данных типа long, выражающих время перелета в милисекундах
     * @param proc процентное значение для расчета процентиля
     * @return первое значение из упорядоченного списка list превышающее процентиля
     *//*
    private static Long getProcentil(List<Long> list, double proc){
        Collections.sort(list);
        int index = (int)(list.size() * proc / 100);
        if (index < list.size() - 1) index++;
        return list.get(index);
    }*/

    /**
     * Возвращает значение не принадлежащее списку list, являющее на единицу времени (по файлу json минимальная единица времени - 1 минута)
     * больше грацины процентиля в списке
     *
     * @param list список данных типа long, выражающих время перелета в милисекундах
     * @param proc процентное значение для расчета процентиля
     * @return процентиль
     */
    private static Long getProcentil(List<Long> list, double proc) {
        Collections.sort(list);
        int index = (int) (list.size() * proc / 100);
        if (index < list.size() - 1) index++;
        return list.get(index) + 60 * 1000;
    }
}

