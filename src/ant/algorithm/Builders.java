package ant.algorithm;

import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.util.Duration;

public class Builders
{
    public TranslateTransition build_translate_transition( Image_ant apply_to, double animation_speed, int new_pos_x, int new_pos_y, GrafLine line )
    {
        return TranslateTransitionBuilder.create().duration( Duration.millis( line.get_length() / animation_speed ) ).autoReverse( false ).cycleCount( 1 ).node( apply_to ).targetFramerate( 30 ).fromX( apply_to.getTranslateX() ).fromY( apply_to.getTranslateY() ).toX( new_pos_x ).toY( new_pos_y ).autoReverse( false ).cycleCount( 1 ).build();
    }
    public Image_ant build_image_ant( int id, int pos_x, int pos_y, String name )
    {
        Image_ant ant = new Image_ant( id, pos_x, pos_y, name );
        return ant;
    }
    public Text build_text( String text )
    {
        return TextBuilder.create().text( text ).build();
    }
    public Circle build_circle_ex()
    {
        return CircleBuilder.create().build();
    }
    public Circle build_circle_grid( int id, int pos_x, int pos_y )
    {
        return CircleBuilder.create().id( id + "" ).cursor( Cursor.HAND ).centerX( pos_x ).centerY( pos_y ).radius( Colors_and_shapes.radius_grid ).fill( Colors_and_shapes.color_grid ).build();
    }
    public GrafLine build_line( String id, double start_x, double start_y, double end_x, double end_y, double b, int komsu_1, int komsu_2 )
    {
        GrafLine new_line = new GrafLine( id, start_x, start_y, end_x, end_y, b, komsu_1, komsu_2 );
        return new_line;
    }
    public ImageView build_image()
    {
        return ImageViewBuilder.create().fitWidth( 20 ).fitHeight( 20 ).image( new Image( AntAlgorithm.class.getResourceAsStream( "imgs/ant.png" ) ) ).build();
    }
    private double total_length( double translateX, double translateY, int new_pos_x, int new_pos_y )
    {
        return Math.sqrt( Math.pow( Math.abs( translateX - translateY ), 2 ) + Math.pow( Math.abs( new_pos_x - new_pos_y ), 2 ) );
    }
}
