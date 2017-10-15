package ant.algorithm;

import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Image_ant extends ImageView
{
    private boolean found_food = false;
    private String last_point, name;
    private ArrayList visited_paths;
    private Operations oopsyy;
    public Image_ant( int id, int pos_x, int pos_y, String name )
    {
        this.setId( id + "" );
        this.set_name( name );
        this.setTranslateX( pos_x - 10 );
        this.setTranslateY( pos_y - 10 );
        this.last_point = id + "";
        this.setImage( new Image( AntAlgorithm.class.getResourceAsStream( "imgs/ant.png" ) ) );
        this.setFitWidth( 20 );
        this.setFitHeight( 20 );
        this.setSmooth( true );
        visited_paths = new ArrayList();
        oopsyy = new Operations();
    }
    public String get_name()
    {
        return name;
    }
    public void set_name( String name )
    {
        this.name = name;
    }
    public String get_last_id()
    {
        return last_point;
    }
    public void set_last_id( String last_edge )
    {
        this.last_point = last_edge;
    }
    public void set_found_food( boolean value )
    {
        this.found_food = value;
    }
    public void clear_paths()
    {
        visited_paths.clear();
        set_last_id( "-1" );
    }
    public ArrayList get_paths()
    {
        return visited_paths;
    }
    public boolean is_found_food()
    {
        return found_food;
    }
    public boolean path_is_available( int id_to_go )
    {
        boolean is_suitable = true;
        if ( visited_paths.contains( oopsyy.id_calc( id_to_go, Integer.parseInt( getId() ) ) ) )
        {
            is_suitable = false;
        }
        return is_suitable;
    }
    public void add_path( String calculated_path )
    {
        if ( !visited_paths.contains( calculated_path ) )
        {
            visited_paths.add( calculated_path );
        }
    }
    public boolean is_any_path_suitable( ArrayList neighbours )
    {
        boolean is_suitable = false;
        for ( int m = 0; m < neighbours.size(); m++ )
        {
            is_suitable = !visited_paths.contains( oopsyy.id_calc( Integer.parseInt( getId() ), Integer.parseInt( neighbours.get( m ).toString() ) ) );
            if ( is_suitable )
            {
                break;
            }
        }
        return is_suitable || visited_paths.isEmpty();
    }
}
