package ant.algorithm;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

public class GrafLine extends Line
{
    private double line_length, start_x, start_y, end_x, end_y, opacity_minimum = 0.1, feromon_minimum = 1, pheromone_evaporation;
    private SimpleDoubleProperty pheromone_amount;
    private int neighbour_1, neighbour_2;
    private Text new_text;
    public GrafLine( String id, double startX, double startY, double endX, double endY, double evaporation_amount, int n_1, int n_2 )
    {
        super( startX, startY, endX, endY );
        this.setId( id );
        this.setStrokeWidth( Colors_and_shapes.stroke_line );
        this.setCursor( Cursor.HAND );
        this.start_x = startX;
        this.start_y = startY;
        this.end_x = endX;
        this.end_y = endY;
        this.pheromone_evaporation = 1 - ( evaporation_amount / 100 );
        this.setStroke( Color.rgb( 255, 0, 0, opacity_minimum ) );
        this.line_length = Math.sqrt( Math.pow( Math.abs( start_x - end_x ), 2 ) + Math.pow( Math.abs( start_y - end_y ), 2 ) );
        this.pheromone_amount = new SimpleDoubleProperty( feromon_minimum );
        this.pheromone_amount.addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                new_text.setText( Math.round( newValue.doubleValue() ) + " - " + ( int ) line_length );
            }
        } );
        this.neighbour_1 = n_1;
        this.neighbour_2 = n_2;
    }
    public int get_neighbour_1()
    {
        return this.neighbour_1;
    }
    public int get_neighbour_2()
    {
        return this.neighbour_2;
    }
    public void update_feromon()
    {
        pheromone_amount.set( feromon_minimum + ( pheromone_amount.doubleValue() * pheromone_evaporation ) );
        Color line_color = ( Color ) this.getStroke();
        double opacity = calculate_opacity();
        line_color = Color.rgb( 255, ( int ) line_color.getGreen(), ( int ) line_color.getBlue(), opacity );
        this.setStroke( line_color );
    }
    public void update_feromon_2( double q_coef, int length_of_line )
    {
        pheromone_amount.set( pheromone_amount.doubleValue() + ( double ) ( q_coef / length_of_line ) );
        Color line_color = ( Color ) this.getStroke();
        double opacity = calculate_opacity();
        line_color = Color.rgb( 255, ( int ) line_color.getGreen(), ( int ) line_color.getBlue(), opacity );
        this.setStroke( line_color );
    }
    public double get_feromon()
    {
        return pheromone_amount.doubleValue();
    }
    public void set_feromon_zero()
    {
        this.pheromone_amount.set( feromon_minimum );
        this.setStroke( Color.rgb( 255, 0, 0, opacity_minimum ) );
    }
    public double get_length()
    {
        return line_length;
    }
    private double calculate_opacity()
    {
        double sonuc = pheromone_amount.doubleValue() / 100;
        if ( sonuc < opacity_minimum )
        {
            sonuc = opacity_minimum;
        }
        if ( sonuc > 1 )
        {
            sonuc = 1;
        }
        return sonuc;
    }
    public void set_evaporation( double evaporation_amount )
    {
        this.pheromone_evaporation = 1 - ( evaporation_amount / 100 );
    }
    public Text text_weight()
    {
        new_text = TextBuilder.create().build();
        new_text.setText( Math.round( pheromone_amount.doubleValue() ) + " - " + ( int ) line_length );
        if ( this.getStartX() > this.getEndX() )
        {
            new_text.setLayoutX( this.getStartX() - ( Math.abs( this.getStartX() - this.getEndX() ) / 2 ) );
        }
        else
        {
            new_text.setLayoutX( this.getStartX() + ( Math.abs( this.getStartX() - this.getEndX() ) / 2 ) );
        }
        if ( this.getStartY() > this.getEndY() )
        {
            new_text.setLayoutY( this.getStartY() - ( Math.abs( this.getStartY() - this.getEndY() ) / 2 ) );
        }
        else
        {
            new_text.setLayoutY( this.getStartY() + ( Math.abs( this.getStartY() - this.getEndY() ) / 2 ) );
        }
        new_text.visibleProperty().bind( this.visibleProperty() );
        return new_text;
    }
}
