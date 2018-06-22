//
//  WallViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 03/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>

#import "WallViewController.h"
#import "CreateJournalViewController.h"
#import "JournalsViewController.h"
#import "InformationJournalViewController.h"
#import "AddElementViewController.h"
#import "Element.h"
#import "FacebookFriend.h"
#import "Journal.h"
#import "ServerCalls.h"
#import "MapsViewController.h"
#import "Journals.h"
#import "PageViewCell.h"
#import "MemoryCacheUtility.h"
#import "Utility.h"
#import "PopupAddElementViewController.h"

#import "UIViewController+MMDrawerController.h"

#define pageIdentifier @"PageViewCell"

@import GoogleMaps;

@interface WallViewController (){
    NSUserDefaults *_preferences;
    Element *_elementSelected;
    NSDate *_actualDate;
}
@end


@implementation WallViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.navigationController setNavigationBarHidden:NO];
    
    // da inserire il nome del journal scelto
    self.navigationItem.title = @"";
    
    [self initJournalCollectionView];
    [self initDaysView];
    

}


-(void) initJournalCollectionView{
    // inizializzo il layout per la collection view
    UICollectionViewFlowLayout *layout=[[UICollectionViewFlowLayout alloc] init];
    [layout setScrollDirection:UICollectionViewScrollDirectionHorizontal];
    // prendo la grandezza del nib e la setto nella collection
    UINib *cellNib = [UINib nibWithNibName:pageIdentifier bundle:nil];
    
    UIView *rootView = [[cellNib instantiateWithOwner:nil options:nil] lastObject];
    NSValue *size = [NSValue valueWithCGSize:rootView.frame.size];
    [layout setItemSize:[size CGSizeValue]];
    // inizializzo la collection view
    _journalCollectionView=[[UICollectionView alloc] initWithFrame:self.journalCollectionContainer.frame collectionViewLayout:layout];
    [_journalCollectionView setBackgroundColor:[UIColor colorWithWhite:1 alpha:0]];
    [_journalCollectionView setDataSource:self];
    [_journalCollectionView setDelegate:self];
    UINib *cellNib2 = [UINib nibWithNibName:pageIdentifier bundle:nil];
    [_journalCollectionView registerNib:cellNib2 forCellWithReuseIdentifier:pageIdentifier];
    
    // aggiungo la collection view
    [self.view addSubview:_journalCollectionView];
}

-(void) initDaysView{
    _daysView = [[[NSBundle mainBundle] loadNibNamed:@"DayView" owner:self options:nil] objectAtIndex:0];

    _daysView = [[DayView alloc] initWithFrame:self.daysCollectionContainer.frame];
    self.daysCollectionContainer.backgroundColor = [UIColor clearColor];

    _daysView.delegate = self;
    _daysView.dataSource = self;
    
    _daysView.stopAtItemBoundary = YES;
    _daysView.pagingEnabled = YES;
    _daysView.type = iCarouselTypeLinear;
    // aggiungo la view
    [self.view addSubview:_daysView];
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:YES];
    
    if ([[Journals getInstance] getCurrentJournal] == nil){
        // caricamento preferences
        _preferences = [NSUserDefaults standardUserDefaults];
        NSString *lastJournalName = @"last_journal_name";
        NSString *lastJournalOwner = @"last_journal_owner";
        if ([_preferences valueForKey:lastJournalName] != nil && [_preferences valueForKey:lastJournalOwner]){
            // converto lo UserID in un long long
            NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
            [f setNumberStyle:NSNumberFormatterDecimalStyle];
            NSNumber * ownerJournal = [f numberFromString:[_preferences valueForKey:lastJournalOwner]];
            JournalID *newJournalID = [[JournalID alloc] initWithName:[_preferences valueForKey:lastJournalName] andOwnerId:[ownerJournal longLongValue]];
            Journal *lastJournal = [[Journals getInstance] getJournal:newJournalID];
            if (lastJournal != nil){
                [[Journals getInstance] setCurrentJournal:newJournalID];
                [self refreshCurrentJournal];
                NSLog(@"LAST JOURNAL OPEN");
            } else if ([[Journals getInstance] getJournals].count == 0){
                NSLog(@"Go To Create Journal");
                [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToCreateJournal" sender:nil];
            } else {
                [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToJournals" sender:nil];
            }
        } else if ([[Journals getInstance] getJournals].count == 0){
            NSLog(@"Go To Create Journal");
            [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToCreateJournal" sender:nil];
        } else {
            [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToJournals" sender:nil];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void) refreshCurrentJournal {
    
    // scarico gli elementi dal server se non sono in cache
    Journal *currentJournal = [[Journals getInstance] getCurrentJournal];
    [self downloadJournalContents : currentJournal.elements];
        
    if (currentJournal.elements.count == 0){
        // ricarico l'UI con i nuovi dati
        dispatch_async(dispatch_get_main_queue(), ^{
            _journalCollectionView.hidden = YES;
            _daysView.hidden = YES;
            _daysCollectionContainer.backgroundColor = [UIColor clearColor];
            _noElementView.hidden = NO;
        });
    } else {
        // ricarico l'UI con i nuovi dati
        dispatch_async(dispatch_get_main_queue(), ^{
            _noElementView.hidden = YES;
            _journalCollectionView.hidden = NO;
            _daysView.hidden = NO;
            self.daysCollectionContainer.backgroundColor = [UIColor colorWithRed:0x28/255.0f green:0x35/255.0f blue:0x93/255.0f alpha:1.0];
            [_journalCollectionView reloadData];
            [_daysView reloadData];
        });
    }

    // setto il nome del journal come titolo
    self.navigationItem.title = currentJournal.name;
}


- (void) downloadJournalContents : (NSArray<Element *> *) elements {
    Journal *currentJournal = [[Journals getInstance] getCurrentJournal];
    ServerCalls* serverCalls = [ServerCalls getInstance];
    
    for (int i = 0; i < elements.count; i++) {
        Element *element = elements[i];
        NSString *content = [MemoryCacheUtility getElementContentFromCache : element];
        [element setContent : content];
        if (content == nil) {
            // l'elemento non e'in cache, lo scarico dal server
            [serverCalls downloadElement:^(Element *downloadedElement) {
                // elemento scaricato, lo salvo in cache
                NSString *cachedContent = [MemoryCacheUtility cacheElementContent: downloadedElement];
                element.content = cachedContent;
                element.idElement = downloadedElement.idElement;
                // aggiorno la PageView di questo elemento, se il journal mostrato e' ancora quello
                if([[Journals getInstance] getCurrentJournal] == currentJournal){
                    //NSArray *array = [[NSArray alloc] initWithObjects:[NSIndexPath indexPathForRow: i inSection: 0], nil];
                    //[_journalCollectionView reloadItemsAtIndexPaths:array];
                    [_journalCollectionView reloadData];
                }
            } andJournalOwnerID: currentJournal.owner_id andJournalName: currentJournal.name andElementPosition: &i];
        }
    }
}
- (IBAction)openPopUpAddElement:(id)sender {
    [self performSegueWithIdentifier:@"popupAddElementSegue" sender:nil];
}

- (IBAction)goToInformationJournal:(id)sender {
    [self performSegueWithIdentifier:@"fromWallToInformationJournal" sender:nil];
}

- (IBAction)refreshItem:(id)sender {
    [[ServerCalls getInstance] downloadJournal:^(Journal * journal) {
        [[Journals getInstance] updateJournal:journal];
        // setto la data all'ultima
        _actualDate = [[[Journals getInstance] getCurrentJournal] getLastDate];
        // refresho il journal
        [self refreshCurrentJournal];
    } andJournalOwnerID:[[Journals getInstance] getCurrentJournal].owner_id andJournalName:[[Journals getInstance] getCurrentJournal].name];
}

// BEGIN: Methods for collection view
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    return [self journalCellForItemAtIndexPath:indexPath];

}

-(UICollectionViewCell *)journalCellForItemAtIndexPath:(NSIndexPath *)indexPath{
    PageViewCell *cell = (PageViewCell *)[_journalCollectionView dequeueReusableCellWithReuseIdentifier:pageIdentifier forIndexPath:indexPath];
    cell.contentView.frame = [cell bounds];
    
    if ([[[Journals getInstance] getCurrentJournal] getElementsOfDay:_actualDate].count != 0){
        // recupero gli elementi del giorno attuale per settarli
        NSMutableArray *currentJournal = [[[Journals getInstance] getCurrentJournal] getElementsOfDay:_actualDate];
        Element *currentElement = currentJournal[indexPath.row];
        
        // Qui si settano i dati della pagina
        [cell fillWithElement: currentElement];
        
        if (cell.placeButton) {
            [cell setDidTapButtonBlock:^(id sender)
             {
                 _elementSelected = currentElement;
                 [self performSegueWithIdentifier:@"fromWallToMapsController" sender:nil];
             }];
        }
        
        // sharing
        if (cell.buttonShare){
            [cell setDidTapButtonBlockSharing:^(id sender) {
                NSArray *items;
                // grab an item we want to share
                switch (currentElement.type) {
                    case NOTE:{
                        NSLog(@"NOTA");
                        NSString *content = [NSString stringWithFormat:@"%@\nSharing with: http://www.travelnotes.com/" , currentElement.content];
                        items = @[content];
                    }
                        break;
                    case IMAGE:{
                        NSLog(@"IMAGE");
                        NSData *data = [Utility loadDataContent:currentElement.content];
                        UIImage *imageToShare = [UIImage imageWithData:data];
                        NSString *url = @"Sharing with: http://www.travelnotes.com/";
                        items = @[imageToShare, url];
                    }
                        break;
                    case VIDEO:{
                        NSLog(@"VIDEO");
                        NSURL *urlToShare = [NSURL fileURLWithPath:currentElement.content isDirectory:NO];
                        NSString *url = @"Sharing with: http://www.travelnotes.com/";
                        items = @[urlToShare, url];
                    }
                        break;
                    default:{
                        NSLog(@"DEFAULT");
                        NSString *content = @"Sharing with: http://www.travelnotes.com/";
                        items = @[content];
                        break;
                    }
                }
                
                // build an activity view controller
                UIActivityViewController *controller = [[UIActivityViewController alloc]initWithActivityItems:items applicationActivities:nil];
                
                // and present it
                [self presentViewController:controller animated:YES completion:^{
                    NSLog(@"Ok sharing");
                }];
            }];
        }
        
        return cell;
    } else {
        return nil;
    }

}

-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    // recupero il numero degli elementi del giorno attuale
    return [[[Journals getInstance] getCurrentJournal] getElementsOfDay:_actualDate].count;
}
// END: Methods for collection view

// utilizzato per tornare indietro in QUESTO VC
-(IBAction) unwindToWallViewController:(UIStoryboardSegue *)segue{
    NSLog(@"Go back to wall VC");
    if ([segue.identifier isEqualToString:@"goBackToWallFromCreateJournal"]){
        // gestisce gli argomenti ritornati da create journal
        CreateJournalViewController* createJournalVC = [segue sourceViewController];
        JournalID* newJournalID = [JournalID alloc];
        newJournalID = createJournalVC->newJournalID;
        [[Journals getInstance] setCurrentJournal:newJournalID];
        
        [[[Journals getInstance] getCurrentJournal] printAll];
        [[ServerCalls getInstance] uploadNewJournal:^(bool uploaded) {
            if(uploaded){
                NSLog(@"New Journal uploaded to server");
            } else {
                [NSException raise:@"Error" format:@"ERROR while uploading New Journal to server"];
            }
        } andNewJournal:[[Journals getInstance] getCurrentJournal]];
        _actualDate = [[Journals getInstance] getCurrentJournal].getLastDate;
        // refresho il journal da vedere
        [self refreshCurrentJournal];
    } else if([segue.identifier isEqualToString:@"goBackToWallFromJournals"]){
        // gestisce gli argomenti ritornati da create journal
        JournalsViewController* journalsVC = [segue sourceViewController];
        JournalID* newJournalID = [JournalID alloc];
        newJournalID = journalsVC->journalIDselected;
        [[Journals getInstance] setCurrentJournal:newJournalID];
        // refresho il journal da vedere
        _actualDate = [[Journals getInstance] getCurrentJournal].getLastDate;
        [self refreshCurrentJournal];
    } else if ([segue.identifier isEqualToString:@"goToWallFromAddElement"]){
        NSLog(@"AGGIUNTA ELEMENT");
        // gestisce gli argomenti ritornati da add element
        AddElementViewController *addElementVC = [segue sourceViewController];
        
        // aggiunge l'elemento al journal
        if(addElementVC->_currentElement.content != nil){
            Element *addedElement = [[Element alloc] initWithElement:addElementVC->_currentElement];
            Journal *currentJournal = [[Journals getInstance] getCurrentJournal];
            [currentJournal addElement:addedElement];
            [self refreshCurrentJournal];
            // uppa l'elemento sul server
            Element *elementToUpload = addElementVC->_currentElement;
            [[ServerCalls getInstance] uploadElementToJournal:^(bool uploaded){
                if(uploaded){
                    NSLog(@"Element uploaded to server");
                } else {
                    [NSException raise:@"Error" format:@"ERROR while uploading Element to server"];
                }
            } andNewElement:elementToUpload andJournalOwnerID:currentJournal.owner_id andJournalName:currentJournal.name andElementOwnerID:[[Utility getCurrentUserFacebookID] stringValue]];
        }
    } else if([segue.identifier isEqualToString:@"goToWallFromInformationJournal"]){
        InformationJournalViewController* infoJournalVC = [segue sourceViewController];
        JournalID* journalIDUpdated = [JournalID alloc];
        journalIDUpdated = infoJournalVC->journalIDUpdated;
        [[Journals getInstance] setCurrentJournal:journalIDUpdated];
        [[[Journals getInstance] getCurrentJournal] printAll];
        // crea un journal vuoto con solo i nuovi partecipanti
        Journal *journalToUpdate = [[Journal alloc] initWithName:[[Journals getInstance] getCurrentJournal].name andOwnerId:[[Journals getInstance] getCurrentJournal].owner_id];
        journalToUpdate.participants = [[Journals getInstance] getCurrentJournal].participants;
        
        [[ServerCalls getInstance] updateJournal:^(bool updated) {
            if(updated){
                NSLog(@"Journal updated to server");
            }
        } andNewJournal:journalToUpdate];
        // refresho il journal da vedere
        [self refreshCurrentJournal];
    }
}

// utilizzato per poter passare i valori all'altro VC prima di passare da un VC all'altro
- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([segue.identifier isEqualToString:@"fromWallToMapsController"]){
        MapsViewController* mapsVC = [segue destinationViewController];
        mapsVC->element = _elementSelected;
        mapsVC->journalID = [[[Journals getInstance] getCurrentJournal] getJournalID];
    } else if([segue.identifier isEqualToString:@"fromWallToAddElement"]){
        // si va alla schermata per l'aggiunta dell'elemento, passandogli che tipo di elemento si vuole aggiungere
        AddElementType type;
        switch ([sender tag]){
            case 1:
                type = WRITE_NOTE;
                break;
            case 2:
                type = CAPTURE_IMAGE;
                break;
            case 3:
                type = CHOOSE_IMAGE;
                break;
            case 4:
                type = CAPTURE_VIDEO;
                break;
            case 5:
                type = CHOOSE_VIDEO;
                break;
            default:
                [NSException raise:@"Error" format:@"Wrong element clicked"];
                break;
        }
        
        AddElementViewController* addElementVC = [segue destinationViewController];
        addElementVC->_addElementType = type;
        
        // chiudo il popup
        [_popoverViewController dismissViewControllerAnimated:YES completion:nil];
    } else if ([segue.identifier isEqualToString: @"popupAddElementSegue"]) {
        // mostra il popup dell'addelement
        _popoverViewController = segue.destinationViewController;
        _popoverViewController.modalPresentationStyle = UIModalPresentationPopover;
        _popoverViewController.popoverPresentationController.delegate = self;
        _popoverViewController.wallViewController = self;
    }
}

- (UIModalPresentationStyle)adaptivePresentationStyleForPresentationController: (UIPresentationController *)controller {
    // Force popover style
    return UIModalPresentationNone;
}

// azione che fa aprire il drawer
- (IBAction)openDrawer:(id)sender {
    [self.mm_drawerController toggleDrawerSide:MMDrawerSideLeft animated:YES completion:nil];
}

#pragma mark iCarousel methods

- (NSInteger)numberOfItemsInCarousel:(iCarousel *)carousel
{
    // recupera il numero dei giorni del journal
    return [[[Journals getInstance] getCurrentJournal] getAllJournalDays].count;
}

- (UIView *)carousel:(iCarousel *)carousel viewForItemAtIndex:(NSInteger)index reusingView:(UIView *)view
{
    DayView *dayView;
    NSMutableArray<NSDate *>* allDays = [[[Journals getInstance] getCurrentJournal] getAllJournalDays];
    
    // se la view è nulla setto il carousel altrimenti riuso la view vecchia
    if (view == Nil)
    {
        dayView = [[[NSBundle mainBundle] loadNibNamed:@"DayView" owner:self options:nil] objectAtIndex:0];
        dayView.frame = _daysView.frame;
    }
    else{
        dayView = (DayView *)view;
    }
    
    if ([_actualDate compare:[allDays objectAtIndex:index]] != NSOrderedAscending){
        [dayView fillWithDate:[allDays objectAtIndex:index]];
        if (allDays.count == 1){
            [dayView hideLeftArrow:YES];
            [dayView hideRightArrow:YES];
        } else if (index > 0 && index == (allDays.count -1)){
            [dayView hideLeftArrow:NO];
            [dayView hideRightArrow:YES];
        } else if (index == 0 && index < (allDays.count-1)){
            [dayView hideLeftArrow:YES];
            [dayView hideRightArrow:NO];
        } else {
            [dayView hideLeftArrow:NO];
            [dayView hideRightArrow:NO];
        }
    } else {
        [dayView fillWithDate:_actualDate];
        if (allDays.count == 1){
            [dayView hideLeftArrow:YES];
            [dayView hideRightArrow:YES];
        } else if (index > 0 && index == (allDays.count -1)){
            [dayView hideLeftArrow:NO];
            [dayView hideRightArrow:YES];
        } else if (index == 0 && index < (allDays.count-1)){
            [dayView hideLeftArrow:YES];
            [dayView hideRightArrow:NO];
        } else {
            [dayView hideLeftArrow:NO];
            [dayView hideRightArrow:NO];
        }
    }
    
    dayView.userInteractionEnabled = NO;
    return dayView;
}

- (void)carouselCurrentItemIndexDidChange:(iCarousel *)carousel{
    NSMutableArray<NSDate *>* allDays = [[[Journals getInstance] getCurrentJournal] getAllJournalDays];
    _actualDate = allDays[carousel.currentItemIndex];
    [self refreshCurrentJournal];

}
@end
