//
//  AddElementViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 30/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <CoreLocation/CoreLocation.h>

#import "AddElementViewController.h"
#import "PageViewCell.h"
#import "GeoLocationUtility.h"
#import "Journal.h"
#import "Journals.h"
#import "MemoryCacheUtility.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <MobileCoreServices/UTCoreTypes.h>
#import <MediaPlayer/MediaPlayer.h>
#import "Utility.h"

#define pageIdentifier @"PageViewCell"

@interface AddElementViewController ()<CLLocationManagerDelegate>{
    CLLocationManager* _locationManager;
    float _currentLatitude;
    float _currentLongitude;
}

@end

@implementation AddElementViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"Add Element";
    
    _locationManager = [[CLLocationManager alloc] init];
    // delego la classe a poter utilizzare il servizio
    [_locationManager setDelegate:self];
    
    // accuratezza del kilometro
    _locationManager.desiredAccuracy = kCLLocationAccuracyKilometer;
    
    // minimo spostamento di cui vogliamo essere avvisati
    _locationManager.distanceFilter = 50; //50 metri
    
    ElementType elementType;
    switch(_addElementType){
        case CAPTURE_IMAGE:
        case CHOOSE_IMAGE:
            elementType = IMAGE;
            break;
        case CAPTURE_VIDEO:
        case CHOOSE_VIDEO:
            elementType = VIDEO;
            break;
        case WRITE_NOTE:
            elementType = NOTE;
            break;
    }
    
    // creo l'element
    _currentElement = [[Element alloc] initWithElementType:elementType];
    [_currentElement setDate:[NSDate date]];
    [_currentElement setOwnerName:[FBSDKProfile currentProfile].name];
    [_currentElement setContent:nil];
    
    // inizializzo il layout per la collection view
    UICollectionViewFlowLayout *layout=[[UICollectionViewFlowLayout alloc] init];
    [layout setScrollDirection:UICollectionViewScrollDirectionVertical];
    // prendo la grandezza del nib e la setto nella collection
    UINib *cellNib = [UINib nibWithNibName:pageIdentifier bundle:nil];
    
    UIView *rootView = [[cellNib instantiateWithOwner:nil options:nil] lastObject];
    NSValue *size = [NSValue valueWithCGSize:rootView.frame.size];
    [layout setItemSize:[size CGSizeValue]];
    // inizializzo la collection view
    _journalCollectionView = [[UICollectionView alloc] initWithFrame:self.pageConatiner.frame collectionViewLayout:layout];
    [_journalCollectionView setBackgroundColor:[UIColor colorWithWhite:1 alpha:0]];
    [_journalCollectionView setDataSource:self];
    [_journalCollectionView setDelegate:self];
    UINib *cellNib2 = [UINib nibWithNibName:pageIdentifier bundle:nil];
    [_journalCollectionView registerNib:cellNib2 forCellWithReuseIdentifier:pageIdentifier];
    
    // aggiungo la collection view
    [self.view addSubview:_journalCollectionView];
    
    // richiedo di scegliere o catturare immagine/video
    if(elementType != NOTE){
        [self showPickerForSourceType:_addElementType];
    }
    
    // per richiedere la conferma quando l'utente preme indietro
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel
                                                                                          target:self
                                                                                          action:@selector(confirmCancel)];
}

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex{
    if (buttonIndex){
        // The didn't press "no", so pop that view!
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)confirmCancel{
    // Do whatever confirmation logic you want here, the example is a simple alert view
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Warning"
                                                    message:@"Are you sure you want to delete your draft?"
                                                   delegate:self
                                          cancelButtonTitle:@"No"
                                          otherButtonTitles:@"Yes", nil];
    [alert show];
}

// BEGIN: metodi per i delegate dei picker

- (void)showPickerForSourceType:(AddElementType)sourceType
{
    UIImagePickerController *pickerController = [[UIImagePickerController alloc] init];
    
    switch(sourceType){
        case CHOOSE_VIDEO:
            NSLog(@"CHOOSE_VIDEO");
            pickerController.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
            pickerController.delegate = self;
            pickerController.modalPresentationStyle = UIModalPresentationPopover;
            pickerController.mediaTypes = [[NSArray alloc] initWithObjects:(NSString *)kUTTypeMovie, nil];
            pickerController.videoQuality = UIImagePickerControllerQualityTypeLow;
            break;
        case CAPTURE_VIDEO:
            NSLog(@"CAPTURE_VIDEO");
            @try{
                pickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
                pickerController.delegate = self;
                pickerController.modalPresentationStyle = UIModalPresentationFullScreen;
                
                NSArray *mediaTypes = [UIImagePickerController availableMediaTypesForSourceType:UIImagePickerControllerSourceTypeCamera];
                NSArray *videoMediaTypesOnly = [mediaTypes filteredArrayUsingPredicate:[NSPredicate predicateWithFormat:@"(SELF contains %@)", @"movie"]];
                
                if ([videoMediaTypesOnly count] == 0){
                    // non supporta i video, ritorno alla wall activity
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No Video" message:@"This device doesn't support videos." delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                    [alert show];
                    [self performSegueWithIdentifier:@"goToWallFromAddElement" sender:nil];
                    return;
                } else {
                    pickerController.cameraDevice = UIImagePickerControllerCameraDeviceRear;
                    pickerController.mediaTypes = videoMediaTypesOnly;
                    pickerController.videoQuality = UIImagePickerControllerQualityTypeLow;
                }
            } @catch (NSException *exception){
                // non e' presente la camera, ritorno alla wall activity
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No Camera" message:@"Camera is not available." delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                [alert show];
                [self performSegueWithIdentifier:@"goToWallFromAddElement" sender:nil];
                return;
            }
            break;
        case CHOOSE_IMAGE:
            NSLog(@"CHOOSE_IMAGE");
            pickerController.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
            pickerController.delegate = self;
            pickerController.modalPresentationStyle = UIModalPresentationPopover;
            pickerController.mediaTypes = [UIImagePickerController availableMediaTypesForSourceType: UIImagePickerControllerSourceTypeSavedPhotosAlbum];
            break;
        case CAPTURE_IMAGE:
            NSLog(@"CAPTURE_IMAGE");
            @try{
                pickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
                pickerController.delegate = self;
                pickerController.modalPresentationStyle = UIModalPresentationFullScreen;
            } @catch (NSException *exception){
                // non e' presente la camera, ritorno alla wall activity
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No Camera" message:@"Camera is not available  " delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                [alert show];
                [self performSegueWithIdentifier:@"goToWallFromAddElement" sender:nil];
                return;
            }
            break;
        case WRITE_NOTE:
            pickerController = nil;
            break;
    }
    
    _imagePickerController = pickerController;
    [self presentViewController:_imagePickerController animated:YES completion:nil];
}

// viene chiamato quando e' stata scelta un'immagine/video dalla galleria o catturato un immagine/video
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary <NSString *,id> *)info
{
    switch(_addElementType){
        case CHOOSE_IMAGE:{
            UIImage *image = info[UIImagePickerControllerOriginalImage];
            data = [Utility compressImage: image];
            [_page showImage: image];
            break;
        }
        case CAPTURE_IMAGE:{
            UIImage *image = info[UIImagePickerControllerOriginalImage];
            data = [Utility compressImage: image];
            [_page showImage: image];
            break;
        }
        case CHOOSE_VIDEO:{
            NSURL *videoURL = [info valueForKey:UIImagePickerControllerMediaURL];
            // non comprimo il video perche' lo fa gia' ios
            data = [NSData dataWithContentsOfURL: videoURL options:NSDataReadingMappedIfSafe error:nil];
            [_page showVideo: videoURL];
            break;
        }
        case CAPTURE_VIDEO:{
            NSURL *videoURL = [info valueForKey:UIImagePickerControllerMediaURL];
            // non comprimo il video perche' lo fa gia' ios
            data = [NSData dataWithContentsOfURL: videoURL options:NSDataReadingMappedIfSafe error:nil];
            [_page showVideo: videoURL];
            break;
        }
        case WRITE_NOTE:
            break;
    }
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

// viene chiamato quando preme su cancel e non sceglie un immagine
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self dismissViewControllerAnimated:YES completion:nil];
    [self performSegueWithIdentifier:@"goToWallFromAddElement" sender:nil];
}

// END: metodi per i delegate dei picker


// BEGIN: Metodi per la pagina
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    _page = (PageViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:pageIdentifier forIndexPath:indexPath];
    _page.contentView.frame = [_page bounds];
    
    // Qui si settano i dati della pagina
    [_page fillWithElement: _currentElement];
    if(_addElementType == WRITE_NOTE){
        [_page setNoteEditable: YES];
    }
    
    return _page;
}

-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return 1;
}
// END: Metodi per la pagina

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
    // verifica l'autorizzazione del servizio per poter far partire la geolocalizzazione
    if([CLLocationManager locationServicesEnabled]){
        [_locationManager requestWhenInUseAuthorization];
    }else{
        NSLog(@"Location services unavailable");
    }
    
}

- (IBAction)addElementAndGoToWall:(id)sender {
    NSString *content = nil;
    
    switch(_addElementType){
        case WRITE_NOTE:
            if([_page.contentText.text  isEqual: @""]){
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Empty Note" message:@"The note cannot be empty" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                [alert show];
            } else {
                content = _page.contentText.text;
            }
            break;
        case CHOOSE_IMAGE:
            content = [MemoryCacheUtility cacheData: data withExtension:@"png"];
            _currentElement.idElement = [Utility ComputeHash:data];
            break;
        case CHOOSE_VIDEO:
            content = [MemoryCacheUtility cacheData: data withExtension:@"mp4"];
            _currentElement.idElement = [Utility ComputeHash:data];
            break;
        case CAPTURE_IMAGE:
            content = [MemoryCacheUtility cacheData: data withExtension:@"png"];
            _currentElement.idElement = [Utility ComputeHash:data];
            break;
        case CAPTURE_VIDEO:
            content = [MemoryCacheUtility cacheData: data withExtension:@"mp4"];
            _currentElement.idElement = [Utility ComputeHash:data];
            break;
    }
    
    if(content != nil){
        _currentElement.content = content;
        // ritorno alla wall activity
        NSLog(@"GO TO WALL");
        [self performSegueWithIdentifier:@"goToWallFromAddElement" sender:nil];
    }
}

// quando cambia lo stato dell'autorizzazione
-(void) locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status{
    
    // se lo stato è diverso da "non autorizzato" fa partire la geolocalizzazione
    if(status != kCLAuthorizationStatusDenied){
        [_locationManager startUpdatingLocation];
    }else{
        NSLog(@"Not authorized to use location services");
    }
    
}

// azione che viene chiamata quando la locazione cambia
-(void) locationManager:(CLLocationManager *)manager didUpdateLocations:(nonnull NSArray<CLLocation *> *)locations{
    CLLocation *location = [locations lastObject];
    [_currentElement setLatitude: (float)location.coordinate.latitude];
    [_currentElement setLongitude:(float)location.coordinate.longitude];
    [_page setLocation: (float)location.coordinate.latitude andLongitude:location.coordinate.longitude];
    NSLog(@"NUOVE COORDINATE SETTATE PER L'ELEMENTO lat%f - lon%f", location.coordinate.latitude, location.coordinate.longitude);
}

@end
