package ru.russeb.json.student;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class StudentParserTest {

    @Test(dataProvider = "invalidJsonObjects",enabled = false)
    public void testParseInvalidJsonObject(String json) {

    }

    @Test(dataProvider = "validJsonObjects")
    public void testParseValidJsonObject(String json) throws JsonParseException {
        JsonResult jsonResult = StudentParser.parse(json);
    }


    @DataProvider(name = "validJsonObjects")
    public Object[] providerValidJsonObjectsWithFullInfo(){
        return new Object[]{
                "{\"students\":[]}",
                "{\"students\":[{\"id\":1,\"name\":\"Виктор Белов\",\"course\":2,\"directionOfStudy\":\"Информатика и вычислительная техника\",\"faculty\":\"Факультет программирования\"}]}"
        };
    }

    @DataProvider(name = "invalidJsonObjects")
    public Object[] providerInvalidJsonObjects(){
        return new Object[]{
        };
    }
}
