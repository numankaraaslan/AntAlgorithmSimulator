package ant.algorithm;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;

public class Formulas
{
    private static Stage stage_formul;
    private static ImageView img_feromon, img_path_selection;
    private static Text txt_feromon, txt_path_selection;
    private static Font my_font;
    public static void show()
    {
        my_font = Font.font( "Times New Roman", 22 );
        stage_formul = prepare_stage_formul();
        stage_formul.getIcons().add( new Image( AntAlgorithm.class.getResourceAsStream( "imgs/info.png" ) ) );
        stage_formul.show();
    }
    private static Stage prepare_stage_formul()
    {
        VBox group_formul = prepare_group_formul();
        return StageBuilder.create().fullScreen( false ).resizable( false ).scene( new Scene( group_formul ) ).title( "Formulas" ).width( 640 ).height( 300 ).build();
    }
    private static VBox prepare_group_formul()
    {
        txt_feromon = TextBuilder.create().text( "Feromon maddesi hesaplama formülü (Pheromone formula)" ).font( my_font ).build();
        img_feromon = ImageViewBuilder.create().image( new Image( AntAlgorithm.class.getResourceAsStream( "imgs/feromon_formul.jpg" ) ) ).fitWidth( 360 ).fitHeight( 80 ).build();
        txt_path_selection = TextBuilder.create().text( "Nokta seçimi hesaplama formülü (Path selection)" ).font( my_font ).build();
        img_path_selection = ImageViewBuilder.create().image( new Image( AntAlgorithm.class.getResourceAsStream( "imgs/path_selection.jpg" ) ) ).fitWidth( 250 ).fitHeight( 90 ).build();
        return VBoxBuilder.create().children( txt_feromon, img_feromon, txt_path_selection, img_path_selection ).spacing( 10 ).layoutX( 10 ).layoutY( 10 ).build();
    }
}
