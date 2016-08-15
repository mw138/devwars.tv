package firebase;

import com.bezman.Reference.Reference;
import com.bezman.init.FirebaseInit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class FirebaseConnectionTest {

    @BeforeClass
    public static void setup() {
        FirebaseInit firebaseInit = new FirebaseInit();

        firebaseInit.init();
    }

    @Test
    public void test_firebase_can_connect() {
        FirebaseInit firebaseInit = new FirebaseInit();

        firebaseInit.init();

        assertNotNull(Reference.firebase);
    }

    @Test
    public void test_firebase_can_push_and_get_value() {
        Reference.firebase.setValueAtPath("test", "\"testValue\"");

        assertEquals("\"testValue\"", Reference.firebase.getValueAtPath("test"));
    }

}
