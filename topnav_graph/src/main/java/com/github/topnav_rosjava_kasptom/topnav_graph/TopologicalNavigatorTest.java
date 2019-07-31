package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.PropertyKeys;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.IPropertiesService;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.PropertiesService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TopologicalNavigatorTest {

    private ITopnavNavigator navigator = null;

    @Before
    public void setUp() throws IOException, InvalidRosonNodeIdException, InvalidRosonNodeKindException, InvalidArUcoIdException {
        navigator = createNavigator();
    }

    @Test
    public void graph_createGuidelinesWithEnabledDeadReckoning_correctPath() {
        String startMarkerId = "1";
        String endMarkerId = "2";

        navigator.createGuidelines(startMarkerId, endMarkerId, true, "6200");

        Assert.assertNotNull(navigator.getGuidelines());
    }

    private ITopnavNavigator createNavigator() throws InvalidRosonNodeIdException, InvalidRosonNodeKindException, InvalidArUcoIdException, IOException {
        IPropertiesService propertiesService = PropertiesService.getInstance(null);
        String rosonFileName = propertiesService.getProperty(PropertyKeys.PROPERTY_KEY_ROSON_FILE_PATH);

        RosonParser parser = new RosonParser();
        RosonBuildingDto building = parser.parse(rosonFileName);
        return new TopologicalNavigator(building);
    }
}