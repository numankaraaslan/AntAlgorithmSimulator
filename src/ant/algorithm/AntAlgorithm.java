package ant.algorithm;

import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.Animation.Status;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContextMenuBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuBarBuilder;
import javafx.scene.control.MenuBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleButtonBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.Timer;

public class AntAlgorithm extends Application
{
    private Group group_grid, group_root, group_left;
    private VBox group_right, group_time, group_evaporation, group_q, group_ant_count, group_alpha_beta, group_explanation;
    private HBox group_graf_buttons, group_sim_buttons;
    private Slider slider_animation_timer, slider_feromon_evporation, slider_q_coef, slider_ant_count, slider_alpha, slider_beta;
    private Text txt_animation_timer, txt_feromon_evaporation, txt_q_coef, txt_ant_count, txt_alpha, txt_beta, txt_food_ex, txt_cave_ex, txt_ant_ex, txt_graf_ex, txt_edge_info;
    private ContextMenu contex_menu_circle, contex_menu_edge;
    private MenuItem contex_menu_item_food, contex_menu_item_cave, contex_menu_item_delete_circle, contex_menu_item_delete_edge, menu_item_formuller, menu_item_how_to, menu_item_write_file, menu_item_read_file, menu_item_read_default_file;
    private ToggleButton button_draw_graf;
    private Button button_clear_graf, button_start_sim, button_stop_sim;
    private Circle circle_last, circle_context, circle_food_ex, circle_cave_ex, circle_graf_ex;
    private CheckBox checkbox_show_info;
    private ImageView image_ant_ex;
    private GrafLine line_context;
    private Timer my_timer;
    private Image_ant[] my_little_ants;
    private Circle[][] circle_grid;
    private int[][] all_neihgbours_mat;
    private GrafLine[] edges;
    private Builders my_builder;
    private Scene my_scene;
    private Random my_random;
    private Menu my_menu;
    private Operations Ops;
    private MenuBar my_menu_bar;
    private Stage my_stage;
    private int offset_x = 10, offset_y = 10, graf_space = 45, q_coef = 5000, ant_count = 100, right_width = 450, grid_width, grid_height, screen_width, screen_height;
    private double evaporation = 12, animation_time, alpha = 1.5, beta = -0.2;
    private int animation_time_coef = 20;
    @Override
    public void start( Stage primaryStage )
    {
        my_stage = primaryStage;
        animation_time = ( double ) 1 / animation_time_coef;
        prepare_variables();
        group_root = GroupBuilder.create().layoutX( offset_x ).layoutY( offset_y ).build();
        group_right = prepare_group_right();
        group_left = prepare_group_left();
        my_timer = new Timer( 1000, new ActionListener()
        {
            @Override
            public void actionPerformed( java.awt.event.ActionEvent e )
            {
                Platform.runLater( new Task()
                {
                    @Override
                    protected Object call() throws Exception
                    {
                        feromon_update();
                        return 1;
                    }
                } );
            }
        } );
        group_root.getChildren().addAll( group_left, group_right );
        my_scene = SceneBuilder.create().width( screen_width - 50 ).height( screen_height - 100 ).root( group_root ).onKeyPressed( scene_key_pressed() ).build();
        prepare_stage();
    }
    @Override
    public void stop() throws Exception
    {
        my_timer.stop();
    }
    private void feromon_update()
    {
        for ( int m = 0; m < edges.length; m++ )
        {
            edges[m].update_feromon();
        }
    }
    private void feromon_update( Image_ant image_ant )
    {
        ArrayList temp = image_ant.get_paths();
        int path_length = 0;
        for ( int m = 0; m < temp.size(); m++ )
        {
            for ( int k = 0; k < edges.length; k++ )
            {
                if ( edges[k].getId().equals( temp.get( m ).toString() ) )
                {
                    path_length += edges[k].get_length();
                    break;
                }
            }
        }
        for ( int m = 0; m < temp.size(); m++ )
        {
            for ( int k = 0; k < edges.length; k++ )
            {
                if ( edges[k].getId().equals( temp.get( m ).toString() ) )
                {
                    edges[k].update_feromon_2( q_coef, path_length );
                    break;
                }
            }
        }
    }
    private void create_animation( final Image_ant selected_ant )
    {
        int old_id;
        old_id = Integer.parseInt( selected_ant.getId() );
        if ( circle_grid[old_id / grid_width][old_id % grid_width].getFill() == Colors_and_shapes.color_food )
        {
            if ( !selected_ant.is_found_food() )
            {
                feromon_update( selected_ant );
                selected_ant.clear_paths();
            }
            selected_ant.set_found_food( true );
        }
        else if ( circle_grid[old_id / grid_width][old_id % grid_width].getFill() == Colors_and_shapes.color_cave )
        {
            if ( selected_ant.is_found_food() )
            {
                feromon_update( selected_ant );
                selected_ant.clear_paths();
            }
            selected_ant.set_found_food( false );
        }
        ArrayList ants_suitable_neighbours = new ArrayList();
        ArrayList ants_all_neighbours = new ArrayList();
        for ( int j = 0; j < all_neihgbours_mat[old_id].length; j++ )
        {
            if ( all_neihgbours_mat[old_id][j] == 1 && selected_ant.path_is_suitable( j ) )
            {
                ants_suitable_neighbours.add( j );
            }
            if ( all_neihgbours_mat[old_id][j] == 1 )
            {
                ants_all_neighbours.add( j );
            }
        }
        ArrayList possibilities = new ArrayList();
        int path_to_go = -1;
        if ( ants_all_neighbours.size() == 1 )
        {
            path_to_go = Integer.parseInt( ants_all_neighbours.get( 0 ).toString() );
        }
        else if ( ants_suitable_neighbours.isEmpty() )
        {
            path_to_go = Integer.parseInt( ants_all_neighbours.get( my_random.nextInt( ants_all_neighbours.size() ) ).toString() );
        }
        else if ( ants_suitable_neighbours.size() == 1 )
        {
            path_to_go = Integer.parseInt( ants_suitable_neighbours.get( 0 ).toString() );
        }
        else
        {
            double total_value = 0, neighbour_value;
            for ( int i = 0; i < ants_suitable_neighbours.size(); i++ )
            {
                for ( int k = 0; k < edges.length; k++ )
                {
                    if ( edges[k].getId().equals( Ops.id_calc( old_id, Integer.parseInt( ants_suitable_neighbours.get( i ).toString() ) ) ) )
                    {
                        total_value += Math.pow( edges[k].get_feromon(), alpha ) * Math.pow( edges[k].get_length(), beta );
                        possibilities.add( Math.pow( edges[k].get_feromon(), alpha ) * Math.pow( edges[k].get_length(), beta ) );
                        break;
                    }
                }
            }
            int random_max = 0, temp;
            for ( int i = 0; i < possibilities.size(); i++ )
            {
                neighbour_value = Double.parseDouble( possibilities.get( i ).toString() );
                if ( total_value == 0 )
                {
                    possibilities.set( i, 1 );
                    random_max++;
                }
                else
                {
                    temp = ( int ) ( ( neighbour_value / total_value ) * 1000 ) < 1 ? 1 : ( int ) ( ( neighbour_value / total_value ) * 1000 );
                    possibilities.set( i, temp );
                    random_max += temp;
                }
            }
            int some_random;
            some_random = my_random.nextInt( random_max );
            for ( int k = 0; k < possibilities.size(); k++ )
            {
                some_random -= Double.parseDouble( possibilities.get( k ).toString() );
                if ( some_random < 0 )
                {
                    path_to_go = Integer.parseInt( ants_suitable_neighbours.get( k ).toString() );
                    break;
                }
            }
        }
        selected_ant.setId( path_to_go + "" );
        selected_ant.set_last_id( old_id + "" );
        selected_ant.add_path( Ops.id_calc( old_id, path_to_go ) );
        int new_pos_x = offset_x + ( graf_space * ( path_to_go % grid_width ) ) - 10;
        int new_pos_y = offset_y + ( graf_space * ( path_to_go / grid_width ) ) - 10;
        GrafLine temp_line = edges[0];
        for ( int k = 0; k < edges.length; k++ )
        {
            if ( edges[k].getId().equals( Ops.id_calc( old_id, path_to_go ) ) )
            {
                temp_line = edges[k];
                break;
            }
        }
        TranslateTransition anim = my_builder.build_translate_transition( selected_ant, animation_time, new_pos_x, new_pos_y, temp_line );
        int old_x = ( int ) selected_ant.getTranslateX(), old_y = ( int ) selected_ant.getTranslateY();
        selected_ant.setRotate( Ops.calc_rotate( old_x, old_y, new_pos_x, new_pos_y ) );
        anim.setOnFinished( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                create_animation( selected_ant );
            }
        } );
        selected_ant.setAnimation( anim );
        selected_ant.getAnimation().playFromStart();
    }
    private void reset_neighbours()
    {
        all_neihgbours_mat = new int[ grid_width * grid_height ][ grid_width * grid_height ];
        for ( int m = 0; m < all_neihgbours_mat.length; m++ )
        {
            all_neihgbours_mat[m][m] = 0;
        }
    }
    private void prepare_grid()
    {
        grid_width = ( int ) ( screen_width - offset_x - right_width - graf_space ) / graf_space;
        grid_height = ( int ) ( screen_height - ( 2 * graf_space ) ) / graf_space;
        circle_grid = new Circle[ grid_height ][ grid_width ];
        for ( int m = 0; m < grid_height; m++ )
        {
            for ( int k = 0; k < grid_width; k++ )
            {
                Circle temp_circle = my_builder.build_circle_grid( k + ( grid_width * m ), offset_x + ( graf_space * k ), offset_y + ( graf_space * m ) );
                temp_circle.setOnMouseClicked( graf_mouse_clicked() );
                group_grid.getChildren().add( temp_circle );
                circle_grid[m][k] = temp_circle;
            }
        }
        reset_neighbours();
    }
    private void delete_neighbour( int n_1, int n_2 )
    {
        all_neihgbours_mat[n_1][n_2] = 0;
        all_neihgbours_mat[n_2][n_1] = 0;
    }
    private void delete_neighbour( int n_1 )
    {
        for ( int m = 0; m < edges.length; m++ )
        {
            String[] id = edges[m].getId().split( "," );
            if ( Integer.parseInt( id[0] ) == n_1 || Integer.parseInt( id[1] ) == n_1 )
            {
                edges[m].set_visible( false );
            }
        }
        for ( int m = 0; m < all_neihgbours_mat.length; m++ )
        {
            all_neihgbours_mat[n_1][m] = 0;
            all_neihgbours_mat[m][n_1] = 0;
        }
    }
    private void prepare_variables()
    {
        edges = new GrafLine[ 0 ];
        my_random = new Random();
        Ops = new Operations();
        my_builder = new Builders();
        screen_width = ( int ) Screen.getPrimary().getBounds().getWidth();
        screen_height = ( int ) Screen.getPrimary().getBounds().getHeight();
    }
    private void prepare_stage()
    {
        my_stage.setTitle( "Ant Algorithm Simulator" );
        my_stage.setScene( my_scene );
        my_stage.setWidth( screen_width - 50 );
        my_stage.setHeight( screen_height - 100 );
        my_stage.setFullScreen( true );
        my_stage.getIcons().add( ( new Image( AntAlgorithm.class.getResourceAsStream( "imgs/ant.png" ) ) ) );
        my_stage.show();
    }
    private Group prepare_group_left()
    {
        group_grid = GroupBuilder.create().build();
        prepare_grid();
        group_grid.setLayoutX( 0 );
        group_grid.setLayoutY( 45 );
        my_menu_bar = prepare_menu();
        return GroupBuilder.create().children( my_menu_bar, group_grid ).build();
    }
    private VBox prepare_group_time()
    {
        slider_animation_timer = SliderBuilder.create().blockIncrement( 1 ).showTickMarks( true ).majorTickUnit( 1 ).value( animation_time * animation_time_coef ).min( 0 ).max( 100 ).build();
        slider_animation_timer.valueProperty().addListener( animation_timer_changed() );
        txt_animation_timer = my_builder.build_text( "Animasyon Hızı (Speed) = " + ( int ) slider_animation_timer.getValue() );
        return VBoxBuilder.create().children( txt_animation_timer, slider_animation_timer ).spacing( 5 ).build();
    }
    private VBox prepare_group_evaporation()
    {
        slider_feromon_evporation = SliderBuilder.create().blockIncrement( 1 ).snapToTicks( true ).showTickMarks( true ).majorTickUnit( 1 ).value( evaporation ).min( 1 ).max( 90 ).build();
        txt_feromon_evaporation = my_builder.build_text( "Feromon Uçuculuğu Yüzdesi (Evaporation) (b) = " + ( int ) slider_feromon_evporation.getValue() );
        slider_feromon_evporation.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_feromon_evaporation.setText( "Feromon Uçuculuğu Yüzdesi (Evaporation) (b) = " + newValue.intValue() );
                evaporation = newValue.doubleValue();
                for ( int m = 0; m < edges.length; m++ )
                {
                    edges[m].set_evaporation( evaporation );
                }
            }
        } );
        return VBoxBuilder.create().children( txt_feromon_evaporation, slider_feromon_evporation ).spacing( 5 ).build();
    }
    private VBox prepare_group_q()
    {
        slider_q_coef = SliderBuilder.create().blockIncrement( 1 ).snapToTicks( true ).showTickMarks( false ).majorTickUnit( 1 ).value( q_coef / 100 ).min( 0 ).max( 400 ).build();
        txt_q_coef = my_builder.build_text( "Q sabiti (Pheromone amount) = " + ( int ) slider_q_coef.getValue() );
        slider_q_coef.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_q_coef.setText( "Q sabiti (Pheromone amount) = " + newValue.intValue() );
                q_coef = 100 * newValue.intValue();
            }
        } );
        return VBoxBuilder.create().children( txt_q_coef, slider_q_coef ).spacing( 5 ).build();
    }
    private VBox prepare_group_ant_count()
    {
        slider_ant_count = SliderBuilder.create().blockIncrement( 1 ).snapToTicks( true ).showTickMarks( false ).majorTickUnit( 1 ).value( ant_count ).min( 1 ).max( 3000 ).build();
        txt_ant_count = my_builder.build_text( "Karınca sayısı (Ant count) = " + ant_count );
        slider_ant_count.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_ant_count.setText( "Karınca sayısı (Ant count) = " + newValue.intValue() );
                ant_count = newValue.intValue();
            }
        } );
        return VBoxBuilder.create().children( txt_ant_count, slider_ant_count ).spacing( 5 ).build();
    }
    private VBox prepare_group_info()
    {
        checkbox_show_info = my_builder.build_checkbox( "Feromon verisi (Pheromone info)" );
        checkbox_show_info.setSelected( true );
        checkbox_show_info.selectedProperty().addListener( new ChangeListener<Boolean>()
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue )
            {
                for ( GrafLine edge : edges )
                {
                    edge.get_info_text().setVisible( newValue );
                }
            }
        } );
        circle_food_ex = my_builder.build_circle_ex();
        circle_food_ex.setRadius( Colors_and_shapes.radius_food );
        circle_food_ex.setFill( Colors_and_shapes.color_food );
        txt_food_ex = my_builder.build_text( "Yiyecek Noktası (Food)" );
        image_ant_ex = my_builder.build_image();
        txt_ant_ex = my_builder.build_text( "Karınca (Ant)" );
        circle_cave_ex = my_builder.build_circle_ex();
        circle_cave_ex.setRadius( Colors_and_shapes.radius_cave );
        circle_cave_ex.setFill( Colors_and_shapes.color_cave );
        txt_cave_ex = my_builder.build_text( "Yuva Noktası (Cave)" );
        circle_graf_ex = my_builder.build_circle_ex();
        circle_graf_ex.setRadius( Colors_and_shapes.radius_grid );
        circle_graf_ex.setFill( Colors_and_shapes.color_graf );
        txt_graf_ex = my_builder.build_text( "Graf Noktası (Graph Point)" );
        HBox hbox_1 = HBoxBuilder.create().children( circle_food_ex, txt_food_ex, circle_cave_ex, txt_cave_ex ).spacing( 10 ).alignment( Pos.BASELINE_LEFT ).build();
        HBox hbox_2 = HBoxBuilder.create().children( circle_graf_ex, txt_graf_ex, image_ant_ex, txt_ant_ex ).spacing( 10 ).alignment( Pos.BASELINE_LEFT ).build();
        return VBoxBuilder.create().children( checkbox_show_info, hbox_1, hbox_2 ).spacing( 10 ).build();
    }
    private VBox prepare_group_alpha_beta()
    {
        slider_alpha = SliderBuilder.create().blockIncrement( 0.01 ).snapToTicks( false ).showTickMarks( true ).majorTickUnit( 1 ).value( alpha ).min( 0 ).max( 10 ).build();
        txt_alpha = my_builder.build_text( "Alpha = " + slider_alpha.getValue() );
        slider_alpha.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_alpha.setText( "Alpha = " + new DecimalFormat( "#0.00" ).format( newValue.doubleValue() ) );
                alpha = newValue.doubleValue();
            }
        } );
        slider_beta = SliderBuilder.create().blockIncrement( 0.01 ).snapToTicks( false ).showTickMarks( true ).majorTickUnit( 1 ).value( beta ).min( -1 ).max( 0 ).build();
        txt_beta = my_builder.build_text( "Beta = " + slider_beta.getValue() );
        slider_beta.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_beta.setText( "Beta = " + new DecimalFormat( "#0.00" ).format( newValue.doubleValue() ) );
                beta = newValue.doubleValue();
            }
        } );
        return VBoxBuilder.create().children( txt_alpha, slider_alpha, txt_beta, slider_beta ).spacing( 5 ).build();
    }
    private VBox prepare_group_right()
    {
        group_time = prepare_group_time();
        group_evaporation = prepare_group_evaporation();
        group_q = prepare_group_q();
        group_ant_count = prepare_group_ant_count();
        group_alpha_beta = prepare_group_alpha_beta();
        group_graf_buttons = prepare_group_graf_buttons();
        group_sim_buttons = prepare_group_sim_buttons();
        contex_menu_circle = prepare_contex_menu_circle();
        contex_menu_edge = prepare_contex_menu_edge();
        group_explanation = prepare_group_info();
        txt_edge_info = my_builder.build_text( "" );
        return VBoxBuilder.create().layoutX( screen_width - right_width - 40 ).spacing( 5 ).prefWidth( right_width ).children( group_time, group_ant_count, group_evaporation, group_q, group_alpha_beta, group_graf_buttons, group_sim_buttons, group_explanation, txt_edge_info ).build();
    }
    private HBox prepare_group_graf_buttons()
    {
        button_draw_graf = ToggleButtonBuilder.create().prefWidth( 225 ).prefHeight( 30 ).text( "Graf çiz (Draw)" ).build();
        button_draw_graf.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                if ( ( ( ToggleButton ) event.getSource() ).isSelected() )
                {
                    ( ( ToggleButton ) event.getSource() ).setText( "Graf çiziliyor (Drawing graph)" );
                    button_start_sim.setDisable( true );
                    button_clear_graf.setDisable( true );
                }
                else
                {
                    ( ( ToggleButton ) event.getSource() ).setText( "Graf çiz (Draw graph)" );
                    button_start_sim.setDisable( false );
                    button_clear_graf.setDisable( false );
                }
                circle_last = null;
            }
        } );
        button_clear_graf = ButtonBuilder.create().prefWidth( 225 ).prefHeight( 30 ).text( "Graf temizle (Clear)" ).build();
        button_clear_graf.setOnAction( clear_graf() );
        return HBoxBuilder.create().children( button_draw_graf, button_clear_graf ).spacing( 5 ).build();
    }
    private HBox prepare_group_sim_buttons()
    {
        button_start_sim = ButtonBuilder.create().prefWidth( 225 ).prefHeight( 70 ).text( "Simulasyonu başlat\n(Start)" ).build();
        button_start_sim.setOnAction( start_sim() );
        button_stop_sim = ButtonBuilder.create().prefWidth( 225 ).disable( true ).prefHeight( 70 ).text( "Simulasyonu durdur\n(Stop)" ).onAction( stop_sim() ).build();
        return HBoxBuilder.create().children( button_start_sim, button_stop_sim ).spacing( 5 ).build();
    }
    private ContextMenu prepare_contex_menu_edge()
    {
        contex_menu_item_delete_edge = MenuItemBuilder.create().text( "Yolu sil (Delete edge)" ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                line_context.set_visible( false );
                delete_neighbour( line_context.get_neighbour_1(), line_context.get_neighbour_2() );
            }
        } ).build();
        return ContextMenuBuilder.create().items( contex_menu_item_delete_edge ).build();
    }
    private ContextMenu prepare_contex_menu_circle()
    {
        contex_menu_item_food = MenuItemBuilder.create().text( "Yiyecek noktası (Food)" ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                for ( int m = 0; m < circle_grid.length; m++ )
                {
                    for ( int k = 0; k < circle_grid[m].length; k++ )
                    {
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_food )
                        {
//                            circle_grid[m][k].setFill( Colors_and_shapes.color_graf );
//                            circle_grid[m][k].setRadius( Colors_and_shapes.radius_grid );
                        }
                    }
                }
                circle_context.setFill( Colors_and_shapes.color_food );
                circle_context.setRadius( Colors_and_shapes.radius_food );
            }
        } ).build();
        contex_menu_item_cave = MenuItemBuilder.create().text( "Yuva noktası (Cave)" ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                for ( int m = 0; m < circle_grid.length; m++ )
                {
                    for ( int k = 0; k < circle_grid[m].length; k++ )
                    {
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_cave )
                        {
                            circle_grid[m][k].setFill( Colors_and_shapes.color_graf );
                            circle_grid[m][k].setRadius( Colors_and_shapes.radius_grid );
                        }
                    }
                }
                circle_context.setFill( Colors_and_shapes.color_cave );
                circle_context.setRadius( Colors_and_shapes.radius_cave );
            }
        } ).build();
        contex_menu_item_delete_circle = MenuItemBuilder.create().text( "Noktayı sil (Delete)" ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                circle_context.setFill( Colors_and_shapes.color_grid );
                circle_context.setRadius( Colors_and_shapes.radius_grid );
                delete_neighbour( Integer.parseInt( circle_context.getId() ) );
            }
        } ).build();
        return ContextMenuBuilder.create().items( contex_menu_item_delete_circle, contex_menu_item_food, contex_menu_item_cave ).build();
    }
    private MenuBar prepare_menu()
    {
        menu_item_formuller = MenuItemBuilder.create().text( "Formüller ve açıklamaları (Formulas)" ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                Formulas.show();
            }
        } ).build();
        menu_item_how_to = MenuItemBuilder.create().text( "Nasıl kullanılır (How to)" ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                How_to.show();
            }
        } ).build();
        menu_item_write_file = MenuItemBuilder.create().text( "Graf dosyaya yaz (Write to file)" ).onAction( write_file() ).build();
        menu_item_read_file = MenuItemBuilder.create().text( "Graf dosyadan oku (Read file)" ).onAction( read_file() ).build();
        menu_item_read_default_file = MenuItemBuilder.create().text( "Varsayılan dosyadan oku (Read default file)" ).onAction( read_default_file() ).build();
        my_menu = MenuBuilder.create().text( "Program" ).items( menu_item_formuller, menu_item_how_to, menu_item_write_file, menu_item_read_file, menu_item_read_default_file ).build();
        return MenuBarBuilder.create().menus( my_menu ).useSystemMenuBar( false ).build();
    }
    private EventHandler<MouseEvent> graf_mouse_clicked()
    {
        return new EventHandler<MouseEvent>()
        {
            @Override
            public void handle( MouseEvent event )
            {
                if ( event.getButton() == MouseButton.SECONDARY )
                {
                    Circle clicked = ( Circle ) event.getSource();
                    if ( !button_start_sim.isDisabled() && ( clicked.getFill() == Colors_and_shapes.color_graf || clicked.getFill() == Colors_and_shapes.color_cave || clicked.getFill() == Colors_and_shapes.color_food ) )
                    {
                        circle_context = ( Circle ) event.getSource();
                        contex_menu_circle.show( circle_context, circle_context.getCenterX(), circle_context.getCenterY() );
                        circle_last = null;
                    }
                }
                else if ( button_draw_graf.isSelected() )
                {
                    Circle circle_temp = ( ( Circle ) event.getSource() );
                    circle_temp.setFill( Colors_and_shapes.color_graf );
                    circle_temp.setRadius( Colors_and_shapes.radius_grid );
                    if ( circle_last != null && !circle_last.equals( circle_temp ) )
                    {
                        String id = Ops.id_calc( Integer.parseInt( circle_last.getId() ), Integer.parseInt( circle_temp.getId() ) );
                        GrafLine new_line = my_builder.build_line( id, ( int ) circle_last.getCenterX(), ( int ) circle_last.getCenterY(), ( int ) circle_temp.getCenterX(), ( int ) circle_temp.getCenterY(), evaporation, Integer.parseInt( circle_last.getId() ), Integer.parseInt( circle_temp.getId() ) );
                        new_line.setOnMouseClicked( line_mouse_clicked() );
                        group_grid.getChildren().add( new_line );
                        group_grid.getChildren().add( new_line.text_weight() );
                        new_line.toBack();
                        edges = Ops.add_edge( edges, new_line );
                        all_neihgbours_mat[Integer.parseInt( circle_temp.getId() )][Integer.parseInt( circle_last.getId() )] = 1;
                        all_neihgbours_mat[Integer.parseInt( circle_last.getId() )][Integer.parseInt( circle_temp.getId() )] = 1;
                        circle_last.setFill( Colors_and_shapes.color_graf );
                        circle_last = null;
                    }
                    else
                    {
                        circle_last = circle_temp;
                        circle_last.setFill( Colors_and_shapes.color_last );
                    }
                }
            }
        };
    }
    private EventHandler<MouseEvent> line_mouse_clicked()
    {
        return new EventHandler<MouseEvent>()
        {
            @Override
            public void handle( MouseEvent event )
            {
                if ( event.getButton() == MouseButton.SECONDARY )
                {
                    line_context = ( GrafLine ) event.getSource();
                    contex_menu_edge.show( line_context, event.getScreenX(), event.getScreenY() );
                }
            }
        };
    }
    private EventHandler<KeyEvent> scene_key_pressed()
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle( KeyEvent t )
            {
                if ( t.getText().toLowerCase().equals( "a" ) )
                {
                    slider_animation_timer.setValue( slider_animation_timer.getValue() - 1 );
                }
                if ( t.getText().toLowerCase().equals( "q" ) )
                {
                    slider_animation_timer.setValue( slider_animation_timer.getValue() + 1 );
                }
                if ( t.getText().toLowerCase().equals( "s" ) )
                {
                    slider_feromon_evporation.setValue( slider_feromon_evporation.getValue() - 1 );
                }
                if ( t.getText().toLowerCase().equals( "w" ) )
                {
                    slider_feromon_evporation.setValue( slider_feromon_evporation.getValue() + 1 );
                }
                if ( t.getText().toLowerCase().equals( "d" ) )
                {
                    slider_q_coef.setValue( slider_q_coef.getValue() - 1 );
                }
                if ( t.getText().toLowerCase().equals( "e" ) )
                {
                    slider_q_coef.setValue( slider_q_coef.getValue() + 1 );
                }
                if ( t.getText().toLowerCase().equals( "f" ) )
                {
                    slider_alpha.setValue( slider_alpha.getValue() - 0.01 );
                }
                if ( t.getText().toLowerCase().equals( "r" ) )
                {
                    slider_alpha.setValue( slider_alpha.getValue() + 0.01 );
                }
                if ( t.getText().toLowerCase().equals( "g" ) )
                {
                    slider_beta.setValue( slider_beta.getValue() + 0.01 );
                }
                if ( t.getText().toLowerCase().equals( "t" ) )
                {
                    slider_beta.setValue( slider_beta.getValue() - 0.01 );
                }
            }
        };
    }
    private EventHandler<ActionEvent> read_file()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                String str = Ops.read_file( circle_grid );
                if ( !"".equals( str ) )
                {
                    try
                    {
                        draw_graph( str );
                    }
                    catch ( Exception e )
                    {
                    }
                    get_settings( str );
                }
            }
        };
    }
    private EventHandler<ActionEvent> read_default_file()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                String str = Ops.read_default_file( circle_grid );
                if ( !"".equals( str ) )
                {
                    try
                    {
                        draw_graph( str );
                    }
                    catch ( Exception e )
                    {
                    }
                    get_settings( str );
                }
            }
        };
    }
    private EventHandler<ActionEvent> write_file()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                if ( edges.length > 0 )
                {
                    Ops.write_file( edges, ant_count, evaporation, q_coef, alpha, beta, grid_width );
                    Message_box.show( "Çizilen graf ve ayarlar başarıyla kaydedildi. (Saved)", "Info", Message_box.info_message );
                }
                else
                {
                    Message_box.show( "Önce graf çiziniz!\n(Draw graph first)", "Uyarı", Message_box.warning_message );
                }
            }
        };
    }
    private void get_settings( String str )
    {
        try
        {
            String[] options = str.split( "&" )[1].split( "/" );
            this.ant_count = Integer.parseInt( options[0] );
            slider_ant_count.setValue( ant_count );
            this.evaporation = Double.parseDouble( options[1] );
            slider_feromon_evporation.setValue( evaporation );
            this.q_coef = Integer.parseInt( options[2] );
            slider_q_coef.setValue( q_coef / 100 );
            this.alpha = Double.parseDouble( options[3] );
            slider_alpha.setValue( alpha );
            this.beta = Double.parseDouble( options[4] );
            slider_beta.setValue( beta );
        }
        catch ( Exception e )
        {
        }
    }
    private void draw_graph( String str )
    {
        String[] edge_ids = str.split( "&" )[0].split( "/" );
        String[] temp_id;
        GrafLine new_line;
        int max_x = -1, max_y = -1;
        for ( int m = 0; m < edge_ids.length; m++ )
        {
            temp_id = edge_ids[m].split( "," );
            int x1 = Integer.parseInt( temp_id[0].split( ";" )[0] );
            int y1 = Integer.parseInt( temp_id[0].split( ";" )[1] );
            int x2 = Integer.parseInt( temp_id[1].split( ";" )[0] );
            int y2 = Integer.parseInt( temp_id[1].split( ";" )[1] );
            if ( x1 > max_x )
            {
                max_x = x1;
            }
            if ( x2 > max_x )
            {
                max_x = x2;
            }
            if ( y1 > max_y )
            {
                max_y = y1;
            }
            if ( y2 > max_y )
            {
                max_y = y2;
            }
        }
        if ( max_x > grid_width || max_y > grid_height )
        {
            Message_box.show( "Bu graf bu ekrana çizilemez!\n(Too big to draw on this screen)", "Hata", Message_box.warning_message );
            return;
        }
        clear_graf().handle( null );
        for ( int m = 0; m < edge_ids.length; m++ )
        {
            temp_id = edge_ids[m].split( "," );
            int x1 = Integer.parseInt( temp_id[0].split( ";" )[0] );
            int y1 = Integer.parseInt( temp_id[0].split( ";" )[1] );
            int x2 = Integer.parseInt( temp_id[1].split( ";" )[0] );
            int y2 = Integer.parseInt( temp_id[1].split( ";" )[1] );
            int pos_x_1 = offset_x + ( graf_space * x1 );
            int pos_y_1 = offset_y + ( graf_space * y1 );
            int pos_x_2 = offset_x + ( graf_space * x2 );
            int pos_y_2 = offset_y + ( graf_space * y2 );
            new_line = my_builder.build_line( ( ( y1 * grid_width ) + x1 ) + "," + ( ( y2 * grid_width ) + x2 ), pos_x_1, pos_y_1, pos_x_2, pos_y_2, evaporation, ( y1 * grid_width ) + x1, ( y2 * grid_width ) + x2 );
            new_line.setOnMouseClicked( line_mouse_clicked() );
            group_grid.getChildren().add( new_line );
            group_grid.getChildren().add( new_line.text_weight() );
            new_line.toBack();
            edges = Ops.add_edge( edges, new_line );
            all_neihgbours_mat[( y1 * grid_width ) + x1][( y2 * grid_width ) + x2] = 1;
            all_neihgbours_mat[( y2 * grid_width ) + x2][( y1 * grid_width ) + x1] = 1;
            for ( int k = 0; k < circle_grid.length; k++ )
            {
                for ( int t = 0; t < circle_grid[k].length; t++ )
                {
                    if ( t == x1 && k == y1 )
                    {
                        circle_grid[k][t].setFill( Colors_and_shapes.color_graf );
                    }
                    if ( t == x2 && k == y2 )
                    {
                        circle_grid[k][t].setFill( Colors_and_shapes.color_graf );
                    }
                }
            }
        }
    }
    private EventHandler<ActionEvent> stop_sim()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                my_timer.stop();
                ( ( Button ) event.getSource() ).setDisable( true );
                button_start_sim.setDisable( false );
                for ( int m = 0; m < grid_height; m++ )
                {
                    for ( int k = 0; k < grid_width; k++ )
                    {
                        circle_grid[m][k].setVisible( true );
                    }
                }
                for ( int m = 0; m < my_little_ants.length; m++ )
                {
                    my_little_ants[m].getAnimation().stop();
                    my_little_ants[m].setVisible( false );
                    my_little_ants[m] = null;
                }
                for ( int m = 0; m < edges.length; m++ )
                {
                    edges[m].set_feromon_zero();
                }
                my_little_ants = new Image_ant[ 0 ];
                button_clear_graf.setDisable( false );
                button_draw_graf.setDisable( false );
                slider_ant_count.setDisable( false );
            }
        };
    }
    private EventHandler<ActionEvent> start_sim()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                boolean cave_exists = false, food_sxists = false, graf_exists = false, graf_complete = true;
                for ( int m = 0; m < grid_height; m++ )
                {
                    for ( int k = 0; k < grid_width; k++ )
                    {
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_cave )
                        {
                            cave_exists = true;
                        }
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_food )
                        {
                            food_sxists = true;
                        }
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_graf )
                        {
                            graf_exists = true;
                        }
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_last )
                        {
                            graf_complete = false;
                        }
                    }
                }
                if ( !graf_exists )
                {
                    Message_box.show( "Lütfen graf çiziniz!\n(Draw graph first!)", "Uyarı", Message_box.warning_message );
                    return;
                }
                if ( !graf_complete )
                {
                    Message_box.show( "Lütfen graf çizimini tamamlayınız! Pembe renkli graf noktası kalmamalı.\n(Complete the graph and don't left a pink dot on the graph.)", "Uyarı", Message_box.warning_message );
                    return;
                }
                if ( !cave_exists )
                {
                    Message_box.show( "Yuva noktası ekleyiniz!\nGraf noktasına sağ tıklayarak bu işlemi yapabilirsiniz.\n(Add a cave point by right clicking.)", "Uyarı", Message_box.warning_message );
                    return;
                }
                if ( !food_sxists )
                {
                    Message_box.show( "Yiyecek noktası ekleyiniz!\nGraf noktasına sağ tıklayarak bu işlemi yapabilirsiniz.\n(Add a food point by right clicking.)", "Uyarı", Message_box.warning_message );
                    return;
                }
                button_clear_graf.setDisable( true );
                button_draw_graf.setDisable( true );
                slider_ant_count.setDisable( true );
                my_little_ants = new Image_ant[ 0 ];
                ( ( Button ) event.getSource() ).setDisable( true );
                button_stop_sim.setDisable( false );
                for ( int m = 0; m < edges.length; m++ )
                {
                    edges[m].set_feromon_zero();
                }
                //clear graf
                for ( int m = 0; m < grid_height; m++ )
                {
                    for ( int k = 0; k < grid_width; k++ )
                    {
                        if ( circle_grid[m][k].getFill().equals( Colors_and_shapes.color_grid ) )
                        {
                            circle_grid[m][k].setVisible( false );
                        }
                    }
                }
                //create ants
                boolean cave = false;
                Circle cave_circle = circle_cave_ex;
                for ( int m = 0; m < grid_height; m++ )
                {
                    for ( int k = 0; k < grid_width; k++ )
                    {
                        if ( circle_grid[m][k].getFill() == Colors_and_shapes.color_cave )
                        {
                            cave = true;
                            cave_circle = circle_grid[m][k];
                            break;
                        }
                    }
                    if ( cave )
                    {
                        break;
                    }
                }
                Image_ant ant;
                for ( int m = 0; m < ant_count; m++ )
                {
                    ant = my_builder.build_image_ant( Integer.parseInt( cave_circle.getId() ), ( int ) cave_circle.getCenterX(), ( int ) cave_circle.getCenterY(), "k" + m );
                    group_grid.getChildren().add( ant );
                    my_little_ants = Ops.add_ant( my_little_ants, ant );
                    create_animation( ant );
                }
                my_timer.restart();
            }
        };
    }
    private EventHandler<ActionEvent> clear_graf()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                for ( int m = 0; m < grid_height; m++ )
                {
                    for ( int k = 0; k < grid_width; k++ )
                    {
                        circle_grid[m][k].setFill( Colors_and_shapes.color_grid );
                        circle_grid[m][k].setRadius( Colors_and_shapes.radius_grid );
                    }
                }
                reset_neighbours();
                for ( int m = 0; m < edges.length; m++ )
                {
                    edges[m].set_visible( false );
                    edges[m] = null;
                }
                edges = new GrafLine[ 0 ];
                circle_last = null;
            }
        };
    }
    private ChangeListener<Number> animation_timer_changed()
    {
        return new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_animation_timer.setText( "Animasyon Hızı (Speed) = " + newValue.intValue() );
                animation_time = ( double ) newValue.intValue() / animation_time_coef;
                my_timer.setDelay( animation_time != 0 ? ( int ) ( 500 / animation_time ) : Integer.MAX_VALUE );
                my_timer.restart();
                if ( my_little_ants != null )
                {
                    if ( newValue.intValue() == 0 )
                    {
                        for ( int m = 0; m < my_little_ants.length; m++ )
                        {
                            my_little_ants[m].getAnimation().pause();
                        }
                    }
                    else
                    {
                        for ( int m = 0; m < my_little_ants.length; m++ )
                        {
                            if ( my_little_ants[m].getAnimation().statusProperty().get() == Status.PAUSED )
                            {
                                my_little_ants[m].getAnimation().play();
                            }
                        }
                    }
                }
            }
        };
    }

}
