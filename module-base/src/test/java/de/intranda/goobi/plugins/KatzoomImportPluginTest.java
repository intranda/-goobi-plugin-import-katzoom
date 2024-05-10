package de.intranda.goobi.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.easymock.EasyMock;
import org.goobi.production.enums.ImportType;
import org.goobi.production.importer.Record;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.sub.goobi.config.ConfigurationHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigurationHelper.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*", "jdk.internal.reflect.*" })
public class KatzoomImportPluginTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File tempFolder;
    private static String resourcesFolder;

    @BeforeClass
    public static void setUpClass() throws Exception {
        resourcesFolder = "src/test/resources/"; // for junit tests in eclipse

        if (!Files.exists(Paths.get(resourcesFolder))) {
            resourcesFolder = "target/test-classes/"; // to run mvn test from cli or in jenkins
        }

        String log4jFile = resourcesFolder + "log4j2.xml"; // for junit tests in eclipse

        System.setProperty("log4j.configurationFile", log4jFile);
    }

    @Before
    public void setUp() throws Exception {
        tempFolder = folder.newFolder("tmp");

        resourcesFolder = "src/test/resources/"; // for junit tests in eclipse

        if (!Files.exists(Paths.get(resourcesFolder))) {
            resourcesFolder = "target/test-classes/"; // to run mvn test from cli or in jenkins
        }

        PowerMock.mockStatic(ConfigurationHelper.class);

        ConfigurationHelper configurationHelper = EasyMock.createMock(ConfigurationHelper.class);
        EasyMock.expect(ConfigurationHelper.getInstance()).andReturn(configurationHelper).anyTimes();
        EasyMock.expect(configurationHelper.getConfigurationFolder()).andReturn(resourcesFolder).anyTimes();
        EasyMock.expect(configurationHelper.useS3()).andReturn(false).anyTimes();
        EasyMock.replay(configurationHelper);
        PowerMock.replay(ConfigurationHelper.class);
    }

    @Test
    public void testConstructor() {
        KatzoomImportPlugin plugin = new KatzoomImportPlugin();
        assertNotNull(plugin);
        assertEquals(ImportType.FOLDER, plugin.getImportTypes().get(0));
        plugin.setImportFolder(tempFolder.getAbsolutePath());
    }

    @Test
    public void testGetAllFilenames() {
        KatzoomImportPlugin plugin = new KatzoomImportPlugin();
        List<String> folderList = plugin.getAllFilenames();
        assertEquals(1, folderList.size());
        assertEquals("nka BKA Nominal", folderList.get(0));
    }

    @Test
    public void testGenerateRecordsFromFilenames() {
        KatzoomImportPlugin plugin = new KatzoomImportPlugin();
        List<String> folderList = plugin.getAllFilenames();
        List<Record> recordList = plugin.generateRecordsFromFilenames(folderList);
        assertEquals(0, recordList.size());
    }

}
