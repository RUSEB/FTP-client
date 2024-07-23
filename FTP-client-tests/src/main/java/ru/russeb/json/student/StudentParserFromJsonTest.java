package ru.russeb.json.student;


import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.logging.Logger;

@Test
public class StudentParserFromJsonTest {
    private static final Logger log = Logger.getLogger(StudentParserFromJsonTest.class.getName());

    @Test(dataProvider = "invalidJsonObjectsHasEmptyInfo", expectedExceptions = JsonParseException.class)
    public void testParseInvalidJsonObjectHasEmptyInfo(String json) throws JsonParseException {
        log.info("Testing parseInvalidJsonObjectHasEmptyInfo with json:\n"+json);
        StudentParser.parse(json);
    }
    @Test(dataProvider = "invalidJsonObjectsHasEmptyInfo", expectedExceptions = JsonParseException.class)
    public void testParseInvalidJsonObjectWithFullInfo(String json) throws JsonParseException {
        log.info("Testing parseValidJsonObjectHasEmptyInfo with json:\n"+json);
        StudentParser.parse(json);
    }

    @Test(dataProvider = "validJsonObjectsWithFullInfo")
    public void testParseValidJsonObjectWithFullInfo(String json) throws JsonParseException {
        log.info("Testing parseValidJsonObjectHasFullInfo with json:\n"+json);
        StudentParser.parse(json);
        log.info("OK\n");
    }

    @Test(dataProvider = "validJsonObjectsHasEmptyInfo")
    public void testParseValidJsonObjectsHasEmptyInfo(String json) throws JsonParseException {
        log.info("Testing parseValidJsonObjectHasEmptyInfo with json:\n"+json);
        StudentParser.parse(json);
        log.info("OK\n");
    }


    @DataProvider(name = "validJsonObjectsHasEmptyInfo")
    public Object[] providerValidJsonObjectsHasEmptyInfo(){
        return new Object[]{
                "{\"students\":[{\"id\":1,\"name\":\"Виктор Белов Сергеевич\",\"course\":2,\"directionOfStudy\":\"Информатика и вычислительная техника\",\"faculty\":\"\"}]}",
                "{\"students\":[{\"id\":1,\"name\":\"\",\"course\":1,\"directionOfStudy\":\"Информатика и вычислительная техника\",\"faculty\":\"Факультет программирования\"},{\"id\":2,\"name\":\"Антонов Александр Владиславович\",\"course\":2,\"directionOfStudy\":\"Прикладная математика и информатика\",\"faculty\":\"Факультет математического моделирования и информационных технологий\"},{\"id\":5,\"name\":\"Блинов Ярослав Дмитриевич\",\"course\":4,\"directionOfStudy\":\"Экономика\",\"faculty\":\"Экономический факультет\"}]}",
                "{\"students\":[]}",
        };
    }

    @DataProvider(name = "validJsonObjectsWithFullInfo")
    public Object[] providerValidJsonObjectsWithFullInfo(){
        return new Object[]{
                "{\"students\":[{\"id\":1,\"name\":\"Виктор Белов Сергеевич\",\"course\":2,\"directionOfStudy\":\"Информатика и вычислительная техника\",\"faculty\":\"Факультет программирования\"}]}",
                "{\"students\":[{\"id\":1,\"name\":\"Чернышева Арина Ильинична\",\"course\":1,\"directionOfStudy\":\"Информатика и вычислительная техника\",\"faculty\":\"Факультет программирования\"},{\"id\":2,\"name\":\"Антонов Александр Владиславович\",\"course\":2,\"directionOfStudy\":\"Прикладная математика и информатика\",\"faculty\":\"Факультет математического моделирования и информационных технологий\"},{\"id\":5,\"name\":\"Блинов Ярослав Дмитриевич\",\"course\":4,\"directionOfStudy\":\"Экономика\",\"faculty\":\"Экономический факультет\"}]}"

        };
    }


    @DataProvider(name = "invalidJsonObjectsWithFullInfo")
    public Object[] providerInvalidJsonObjectsWithFullInfo(){
        return new Object[]{
                "{\"students\":[{\"id\":1}]}",
                "{\"students\":[{\"id\":1},{\"id\":2}]}",
                "{\"students\":[\"id\":1,\"name\":\"Виктор Белов Сергеевич\",\"course\":2,\"directionOfStudy\":\"Информатика и вычислительная техника\",\"faculty\"-\"Факультет программирования\"}]}",
        };
    }

    @DataProvider(name = "invalidJsonObjectsHasEmptyInfo")
    public Object[] providerInvalidJsonObjectsHasEmptyInfo(){
        return new Object[]{
                "{students\":[]}",
                "",
                "{students:[]}",
                "{\"id\":3,\"name\":\"Степанова Мирослава Владиславовна\",\"course\":1,\"directionOfStudy\":\"\",\"faculty\":\"\"}",
                "{\"id\":4,\"name\":\"\",\"course\":0,\"directionOfStudy\":\"\",\"faculty\":\"\"}"
        };
    }
}
