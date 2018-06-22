//
//  MapsViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 11/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "MapsViewController.h"
#import "Journal.h"
#import "Journals.h"
#import "CustomInfoWindow.h"
#import "Utility.h"
#import "FontUtility.h"

@implementation MapsViewController{
    NSMutableDictionary *_markerElementHashMap;
    Journal *_journal;
    GMSMapView *_mapView;
}

- (void)viewDidLoad {
    self.navigationItem.title = @"Journal Map";
    [super viewDidLoad];
    _markerElementHashMap = [[NSMutableDictionary alloc] init];
    _journal = [[Journals getInstance] getJournal:journalID];
    [self loadThisView];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


- (void)loadThisView {
    // prendo le coordinate dell'element selezionato
    CLLocationCoordinate2D di = CLLocationCoordinate2DMake(element.latitude, element.longitude);
    // settaggio camera
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:di.latitude
                                                            longitude:di.longitude
                                                                 zoom:15];
    _mapView = [GMSMapView mapWithFrame:CGRectZero camera:camera];
    self.view = _mapView;
    // settaggio info adapter
    _mapView.delegate = self;
    
    // settaggio marker
    GMSMarker *marker = [[GMSMarker alloc] init];
    marker.position = di;
    
    // inserimento nell'hash map
    NSString *key = [NSString stringWithFormat:@"%f,%f",marker.position.latitude,marker.position.longitude];
    [_markerElementHashMap setObject:element forKey:key];

    marker.infoWindowAnchor = CGPointMake(0.5, 0.2);
    
    marker.map = _mapView;
    
    //Seleziona l'elemento
    [_mapView setSelectedMarker:marker];
    
    // tutti gli altri pin setta il marker
    for (Element *element_journal in _journal.elements){
        CLLocationCoordinate2D new_di = CLLocationCoordinate2DMake(element_journal.latitude, element_journal.longitude);
        if(!(di.latitude == new_di.latitude && di.longitude == new_di.longitude)){
            GMSMarker *new_marker = [[GMSMarker alloc] init];
            new_marker.position = new_di;
            new_marker.icon = [GMSMarker markerImageWithColor:[UIColor blueColor]];
            NSString *key = [NSString stringWithFormat:@"%f,%f",new_marker.position.latitude,new_marker.position.longitude];
            [_markerElementHashMap setObject:element_journal forKey:key];
            new_marker.infoWindowAnchor = CGPointMake(0.5, 0.2);
            new_marker.map = _mapView;
        }
    }
}

#pragma mark - GMSMapViewDelegate

// mostra l'info window per il marker selezionato
- (UIView *)mapView:(GMSMapView *)mapView markerInfoWindow:(GMSMarker *)marker {
    // prendo l'elemento dal marker
    NSString *key = [NSString stringWithFormat:@"%f,%f",marker.position.latitude,marker.position.longitude];
    Element *element_journal = [_markerElementHashMap objectForKey:key];
    
    // setto l'info window
    CustomInfoWindow *infoWindow = [[[NSBundle mainBundle] loadNibNamed:@"InfoWindow" owner:self options:nil] objectAtIndex:0];
    
    // settaggio font
    infoWindow.nameLabel.font = [[FontUtility getInstance] getFont:TITLE andSize:17];
    infoWindow.hourLabel.font = [[FontUtility getInstance] getFont:EXTRA andSize:11];
    infoWindow.dateLabel.font = [[FontUtility getInstance] getFont:EXTRA andSize:12];
    infoWindow.textContentView.font = [[FontUtility getInstance] getFont:TEXT andSize:14];
    
    infoWindow.nameLabel.text = element_journal.ownerName;
    
    // creazione data e ora da element date
    NSDateFormatter *hour_formatter = [[NSDateFormatter alloc] init];
    [hour_formatter setDateFormat:@"HH:mm"];
    NSString *hour_string = [hour_formatter stringFromDate:element_journal.date];
    NSDateFormatter *date_formatter = [[NSDateFormatter alloc] init];
    [date_formatter setDateFormat:@"dd/MM/yyyy"];
    NSString *date_string = [date_formatter stringFromDate:element_journal.date];

    infoWindow.hourLabel.text = hour_string;
    infoWindow.dateLabel.text = date_string;
    
    // settaggio content a seconda del tipo
    ElementType type = element_journal.type;
    switch (type) {
        case NOTE:
            infoWindow.imageContentView.hidden = YES;
            infoWindow.textContentView.text = element_journal.content;
            break;
        case IMAGE:{
            infoWindow.textContentView.hidden = YES;
            NSData *data = [Utility loadDataContent:element_journal.content];
            infoWindow.imageContentView.image = [UIImage imageWithData:data];
            break;
        }
        case VIDEO:{
            infoWindow.textContentView.hidden = YES;
            infoWindow.imageContentView.image = [Utility generateThumbImage:element_journal.content];
            break;
        }
        default:
            NSLog(@"ERROR");
            break;
    }
    return infoWindow;
}

@end
