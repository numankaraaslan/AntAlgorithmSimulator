package ant.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;

public class Operations
{
    public String id_calc( int num_1, int num_2 )
    {
        if ( num_1 < num_2 )
        {
            return num_1 + "," + num_2;
        }
        else
        {
            return num_2 + "," + num_1;
        }
    }

    public GrafLine[] add_edge( GrafLine[] old_array, GrafLine new_line )
    {
        GrafLine[] new_array = new GrafLine[ old_array.length + 1 ];
        for ( int m = 0; m <= old_array.length; m++ )
        {
            if ( m == old_array.length )
            {
                new_array[m] = new_line;
            }
            else
            {
                new_array[m] = old_array[m];
            }
        }
        return new_array;
    }

    public Image_ant[] add_circle( Image_ant[] old_array, Image_ant new_circle )
    {
        Image_ant[] new_array = new Image_ant[ old_array.length + 1 ];
        for ( int m = 0; m <= old_array.length; m++ )
        {
            if ( m == old_array.length )
            {
                new_array[m] = new_circle;
            }
            else
            {
                new_array[m] = old_array[m];
            }
        }
        return new_array;
    }
    public double calc_rotate( int old_x, int old_y, int new_pos_x, int new_pos_y )
    {
        double degree;
        int diff_x = old_x - new_pos_x;
        int diff_y = old_y - new_pos_y;
        if ( diff_x != 0 && diff_y != 0 )
        {
            degree = 180 - Math.toDegrees( Math.atan( ( double ) diff_x / diff_y ) );
        }
        else
        {
            degree = Math.toDegrees( Math.atan( ( double ) diff_x / diff_y ) );
        }
        return degree;
    }

    public void write_file( GrafLine[] edges, int ant_count, double evaporation, int q_coef, double alpha, double beta, int grid_width )
    {
        FileChooser file_chooser = FileChooserBuilder.create().title( "Graf Kaydet (Save Graph)" ).build();
        File selected_file = file_chooser.showSaveDialog( null );
        if ( selected_file == null )
        {
            return;
        }
        String saved_file = selected_file.getAbsolutePath();
        String str = "", temp;
        BufferedWriter bw;
        try
        {
            bw = new BufferedWriter( new FileWriter( new File( saved_file ), false ) );
            for ( int m = 0; m < edges.length; m++ )
            {
                temp = edges[m].getId();
                temp = Integer.parseInt( temp.split( "," )[0] ) % grid_width + ";" + Integer.parseInt( temp.split( "," )[0] ) / grid_width + "," + Integer.parseInt( temp.split( "," )[1] ) % grid_width + ";" + Integer.parseInt( temp.split( "," )[1] ) / grid_width;
                str += temp + ( m != edges.length - 1 ? "/" : "" );
            }
            bw.write( str );
            bw.newLine();
            bw.write( ant_count + "/" + evaporation + "/" + q_coef + "/" + alpha + "/" + beta );
            bw.close();
        }
        catch ( IOException ex )
        {
        }
    }
    public String read_file( Circle[][] circle_grid )
    {
        FileChooser file_chooser = FileChooserBuilder.create().title( "Graf Oku (Load Graph)" ).build();
        File selected_file = file_chooser.showOpenDialog( null );
        if ( selected_file == null )
        {
            return "";
        }
        String read_file = selected_file.getAbsolutePath();
        String str = "";
        BufferedReader bw;
        try
        {
            bw = new BufferedReader( new FileReader( read_file ) );
            while ( bw.ready() )
            {
                str += bw.readLine() + "&";
            }
            bw.close();
        }
        catch ( IOException ex )
        {
        }
        return str;
    }
}
