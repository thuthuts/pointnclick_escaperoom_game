package project;

import org.testfx.framework.junit.ApplicationTest;

public class TestFX extends ApplicationTest {

/*    private static final Logger log = LogManager.getLogger(TestFX.class);
    private M_CodeController controller;

    *//**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *//*
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/M_CodeView.fxml"));
        this.controller = loader.getController();
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }

    @Test
    public void shouldClickButton(){
        *//*Button btn = (Button) controller.getContainer().getChildren().stream().filter(node -> node.getId().equals("NUM1")).findFirst().get();
        clickOn(btn);*//*
        //Assert.assertEquals("1", controller.getDisplay().getText());
        //FxAssert.verifyThat(controller.getDisplay(), .hasText("1"));

    }*/
}
