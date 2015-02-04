package com.junbo.common.id.util

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.AddressId
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Link
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by Zhanxin on 5/15/2014.
 */
class IdUtilTest {
    @Test
    void testFromHref() {
        Link link = new Link(href: '/v1/addresses/6b54fdb4bc9f', id: '6b54fdb4bc9f')
        Id id = IdUtil.fromLink(link)
        Assert.assertTrue(id instanceof AddressId)
        Assert.assertEquals(id.value, 33570816L)
    }

    @Test
    void testToHref() {
        AddressId id = new AddressId(33570816L)
        String href = IdUtil.toHref(id)
        Assert.assertEquals(href, '/v1/addresses/6b54fdb4bc9f')
    }

    //@Test
    void testUserId() {
        UserId id = new UserId(32768L)
        String href = IdUtil.toHref(id)
        Assert.assertEquals(href, '/v1/addresses/6b54fdb4bc9f')
    }

    /*
    @Test
    void testUserNickNameIdPair() {
        String filePath = "D:\\junbo\\main\\couchdb\\users.txt";
        readFile(filePath);
    }

    public static void writeFile() throws IOException {
        File fout = new File("out.txt");
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < 10; i++) {
            bw.write("something");
            bw.newLine();
        }

        bw.close();
    }

    private static void readFile(String filePath) throws IOException {
        try {
            File fout = new File("D:\\junbo\\main\\couchdb\\out.txt");
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = reader.readLine()) != null) {
                JsonNode jsonNode = ObjectMapperProvider.instance().readTree(line);

                //System.out.println("id=" + new UserId(Long.parseLong(jsonNode.get("doc").get("_id").toString().replace("\"", ""))).getValue().toString());
                //System.out.println("nickName=" + jsonNode.get("doc").get("nickName"));
                if (jsonNode.get("id").toString().contains("_design")) continue;

                UserId userId = new UserId(Long.parseLong(jsonNode.get("doc").get("_id").toString().replace("\"", "")));

                bw.write(IdUtil.toHref(userId).replace("/v1/users/", "") + "\t" + jsonNode.get("doc").get("nickName"));
                bw.newLine();

               // System.out.println(line);
            }

            bw.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
    */
}
