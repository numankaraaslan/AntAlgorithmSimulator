package ant.algorithm;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;

public class How_to
{
    private static Stage stage_how_to;
    private static Text txt_how_to;
    private static Font my_font;
    public static void show()
    {
        my_font = Font.font( "Times New Roman", 22 );
        stage_how_to = prepare_stage_how_to();
        stage_how_to.show();
    }
    private static Stage prepare_stage_how_to()
    {
        VBox group_fomuller = prepare_group_how_toler();
        return StageBuilder.create().fullScreen( false ).resizable( false ).scene( new Scene( group_fomuller ) ).title( "Formulas" ).width( 900 ).height( 700 ).build();
    }
    private static VBox prepare_group_how_toler()
    {
        String how_too;
        how_too = "TR\nKarınca Algoritması Simülasyonu karınca algoritmasının bir graf üzerinde en kısa yol problemine çözüm üretmesini simülasyon olarak göstermektedir.\nSimülasyonu kullanmak için öncelikle Graf Çiz butonuna tıklayın ve sol taraftaki grid üzerinde iki noktaya tıklayarak graf çizin. Daha sonra yeşil renkte gösterilen graf noktaları üzerine sağ tıklayarak yiyecek ve yuva noktası belirleyin.\nSimülasyonu başlatmadan önce karınca sayısını ayarlayın. Simülasyon sırasında karınca algoritmasında kullanılan formüllerin parametrelerini ve simülasyon hızını gerçek zamanlı değiştirebilirsiniz. Simülasyon sırasında veya durdurulduğunda bir yol üzerine sağ tıklayarak o yolu silebilirsiniz. Simülasyonu durdurup Graf Çiz butonu ile çizdiğiniz graf üzerine yeni node'lar ekleyebilirsiniz.\nSimülasyonda çizdiğiniz Graf yapısını Program menüsünden Graf Dosyaya Yaz seçeneği ile bir dosyaya kaydedebilirsiniz.\nProgram menüsündeki Graf Dosyadan Oku seçeneği ile kayıtlı bir graf yapısını dosyadan okuyup programa aktarabilirsiniz.";
        how_too += "\n\nEN\nThe purpose of this application is to show how ant algorithm simulation is working in realtime\nTo use the simulation, first click on the Draw Graph button and click on two dots on the dotted area. Then right click on any two green dots on the graph and set them as food and cave points.\nSet the ant count before starting the simulation. You can change parameters of the algorithm and the speed of the animations while the simulation is running. You can delete a graph point whilw simulation is running or stopped. You can add new graph points and connect them if you stop the simulation. You can save and load the graph you have drawn in the program menu.";
        txt_how_to = TextBuilder.create().text( how_too ).font( my_font ).textAlignment( TextAlignment.JUSTIFY ).wrappingWidth( 890 ).build();
        return VBoxBuilder.create().children( txt_how_to ).spacing( 10 ).layoutX( 0 ).layoutY( 0 ).build();
    }
}
