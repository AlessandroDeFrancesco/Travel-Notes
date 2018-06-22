//
//  CreateJournalViewController.m
//  Travel Notes
//
//  Created by ianfire on 16/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>

#import "CreateJournalViewController.h"
#import "Journal.h"
#import "Journals.h"
#import "JournalID.h"
#import "FacebookFriend.h"
#import "FriendsTableViewCell.h"
#import "Utility.h"
#import "PopUpDatePickerViewController.h"

@import GooglePlaces;

@implementation CreateJournalViewController{
    GMSPlacesClient *_placesClient;
    NSArray *_pickerTypes;
    NSMutableArray *_friendsList;
    NSMutableArray *_placesList;
    NSDate *_departureDate, *_returnDate;
    PopUpDatePickerViewController *_popoverDatePicker;
}


- (void)viewDidLoad {
    [super viewDidLoad];

    self.navigationItem.title = @"New Journal";
    if ([self->stringFromWall isEqualToString:@"NO ACTIVE JOURNALS"]){
        self.navigationItem.hidesBackButton = YES;
    }

    self.pickerTypeJournal.delegate = self;
    self.pickerTypeJournal.dataSource = self;
    
    self.friendsTable.delegate = self;
    self.friendsTable.dataSource = self;
    
    _friendsList = [[NSMutableArray alloc]init];
    _placesList = [[NSMutableArray alloc] init];
    _placesClient = [GMSPlacesClient sharedClient];
    _pickerTypes = @[@"Car",@"Train",@"Plane",@"Foot",@"Boat",@"Cruise"];
    
    _textPlaces.maxNumberOfResults = 8;
    
    // Per la data
    _returnDate = _departureDate = [NSDate date];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateStyle = NSDateFormatterShortStyle;
    _textReturn.text = _textDeparture.text = [dateFormatter stringFromDate:_returnDate];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(openDatePickerPopUp:)];
    [_buttonDeparture addGestureRecognizer:tapGesture];
    tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(openDatePickerPopUp:)];
    [_buttonReturn addGestureRecognizer:tapGesture];
}

-(void)openDatePickerPopUp: (UITapGestureRecognizer *) sender{
    _popoverDatePicker = [[UIStoryboard storyboardWithName:@"TravelNotesStoryboard" bundle:nil] instantiateViewControllerWithIdentifier:@"popupDatePicker"];
    _popoverDatePicker.createJournalViewController = self;
    
    // per sapere successivamente chi e' stato premuto
    _buttonDeparture.tag = 0;
    _buttonReturn.tag = 0;
    sender.view.tag = 10;
    
    // faccio muovere la scroll view, altrimenti il popup non si vede
    [_scrollView scrollRectToVisible:_createButton.frame animated:NO];
    
    // stile del popup
    _popoverDatePicker.modalPresentationStyle = UIModalPresentationPopover;
    _popoverDatePicker.popoverPresentationController.permittedArrowDirections = UIPopoverArrowDirectionUp;
    _popoverDatePicker.popoverPresentationController.delegate = self;
    _popoverDatePicker.popoverPresentationController.sourceView = sender.view;
    _popoverDatePicker.popoverPresentationController.sourceRect = sender.view.bounds;
    
    // present the popover
    [self presentViewController:_popoverDatePicker animated:true completion:nil];
}

-(void) dateChosen: (NSDate *) date{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateStyle = NSDateFormatterShortStyle;
    
    if(_buttonDeparture.tag == 10){
        _departureDate = date;
        _textDeparture.text = [dateFormatter stringFromDate:date];
    } else {
        _returnDate = date;
        _textReturn.text = [dateFormatter stringFromDate:date];
    }
    // chiudo il popup
    [_popoverDatePicker dismissViewControllerAnimated:YES completion:nil];
}

- (void) viewWillAppear:(BOOL)animated{
    [super viewWillAppear:YES];
    [self loadFriends];
}

-(void) loadFriends{
    NSLog(@"Facebook friend loading...");
    if ([FBSDKAccessToken currentAccessToken]) {
     [[[FBSDKGraphRequest alloc] initWithGraphPath:[NSString stringWithFormat:@"/%@/friends", [[FBSDKProfile currentProfile] userID]] parameters:@{@"fields": @"name, id"}]
      startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error) {
          if (!error) {
              for (NSDictionary *element in result[@"data"]){
                  FacebookFriend *newFacebookFriend = [[FacebookFriend alloc] initWithName:element[@"name"] andIdFriend: element[@"id"]];
                  [_friendsList addObject:newFacebookFriend];
              }
              dispatch_async(dispatch_get_main_queue(), ^{
                  [_friendsTable reloadData];
              });
          }else {
              NSLog(@"%@",error);
          }
      }];
    }
}

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([segue.identifier isEqualToString:@"segueFromCreateJournalToDatePickerPopup"]){
        // mostra il popup del date picker
        _popoverDatePicker = segue.destinationViewController;
        _popoverDatePicker.modalPresentationStyle = UIModalPresentationPopover;
        _popoverDatePicker.popoverPresentationController.delegate = self;
    }
}

- (UIModalPresentationStyle)adaptivePresentationStyleForPresentationController: (UIPresentationController *)controller {
    // Force popover style
    return UIModalPresentationNone;
}

// BEGIN: PICKER METHOD
// The number of columns of data
- (int)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

// The number of rows of data
- (int)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return _pickerTypes.count;
}

// The data to return for the row and component (column) that's being passed in
- (NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return _pickerTypes[row];
}

// END: PICKER METHOD

// BEGIN: TABLE METHOD

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
        return _friendsList.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
        NSString *cellIdentifier = @"FriendFacebookCell";
        FriendsTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
        
        FacebookFriend *friend = _friendsList[indexPath.row];
        NSString *friendName = friend.name;
        cell.friendName.text = friendName;
        
        return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
        FriendsTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
        if(!cell.check){
            [[cell checkBox] setImage:[UIImage imageNamed:@"CheckBox Marked"] forState:UIControlStateNormal];
            cell.check = YES;
            ((FacebookFriend*)_friendsList[indexPath.row]).checked = YES;
        } else if (cell.check) {
            [[cell checkBox] setImage:[UIImage imageNamed:@"CheckBox Empty"] forState:UIControlStateNormal];
            cell.check = NO;
            ((FacebookFriend*)_friendsList[indexPath.row]).checked = NO;
    }

}
// END :TABLE METHOD

// fa autocompletamento dei posti quando si srive sul place journal
- (IBAction)getAutocomplete:(id)sender {
    _textPlaces.clipsToBounds = YES;
    [_placesList removeAllObjects];
    GMSAutocompleteFilter *filter = [[GMSAutocompleteFilter alloc] init];
    filter.type = kGMSPlacesAutocompleteTypeFilterCity;
    [_placesClient autocompleteQuery:self.textPlaces.text
                              bounds:nil
                              filter:filter
                            callback:^(NSArray *results, NSError *error) {
                                if (error != nil) {
                                    NSLog(@"Autocomplete error %@", [error localizedDescription]);
                                    return;
                                }
                                
                                for (GMSAutocompletePrediction* result in results) {
                                    NSLog(@"Result '%@' with placeID %@", result.attributedFullText.string, result.placeID);
                                    if (![_placesList containsObject:result.attributedFullText.string]){
                                        [_placesList addObject: result.attributedFullText.string];
                                    }
                                }
                                [_textPlaces filterStrings:_placesList];
                            }];
    
}

- (IBAction)createJournal:(id)sender {
    if( [Utility isNetworkAvailable] )
    {
        NSLog(@"CONNECTION AVAILABLE, PLEASE WAIT FOR ADD JOURNAL TO SERVER...");
        // recupero il long long dello user id
        NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
        [f setNumberStyle:NSNumberFormatterDecimalStyle];
        NSNumber * fbIDValue = [f numberFromString:[FBSDKProfile currentProfile].userID];
        
        //controlli sulle label
        if ([self checkEmptyTextEdit:self.textNameJournal andErrorString:@"Journal name cannot be empty"] && [self checkEmptyTextEdit:self.textDescriptionJournal andErrorString:@"Journal description cannot be empty"] && [self checkTextContainSpecialChar:self.textNameJournal andErrorString:@"Journal name cannot contain special character"] && [self checkEmptyTextEdit:self.textPlaces andErrorString:@"Journal place cannot be empty"] && [self checkIfDatesAreCorrect]&& [self checkIfNameJournalArleadyExist:self.textNameJournal andOwnerID: [fbIDValue longLongValue] andErrorString:@"Journal already exist, please change name of journal"] ){
            NSLog(@"Go To Wall with New Journal");
            
            Journal *newJournal = [[Journal alloc] initWithName: self.textNameJournal.text andOwnerId:[fbIDValue longLongValue]];
            newJournal.description = self.textDescriptionJournal.text;
            newJournal.departureDate = _departureDate;
            newJournal.returnDate = _returnDate;
            newJournal.city = self.textPlaces.text;
            // recupero informazioni dei picker
            NSInteger selectedRowPickerType = [self.pickerTypeJournal selectedRowInComponent:0];
            newJournal.type = [_pickerTypes objectAtIndex:selectedRowPickerType];
            
            NSMutableArray *friendsSelected = [[NSMutableArray alloc] init];
            // converto lo UserID in un long long
            NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
            [f setNumberStyle:NSNumberFormatterDecimalStyle];
            
            for(int i = 0; i < _friendsList.count;i++){
                FacebookFriend *friend = [FacebookFriend alloc];
                friend = _friendsList[i];
                if (friend.checked){
                    NSNumber * fbIDValue = [f numberFromString:friend.idFriend];
                    long long idFacebookFriend =[fbIDValue longLongValue];
                    [friendsSelected addObject:@(idFacebookFriend)];
                }
            }
            
            NSLog(@"PARTECIPANTI AL JOURNAL : %@", friendsSelected);
            
            newJournal.participants = friendsSelected;
            
            // inserimento nella lista Journals
            self->newJournalID = [[Journals getInstance] addJournal:newJournal];
            NSLog(@"Inserimento effettuato %@", newJournalID);
            // TODO: Sottiscrizione al topic?
            
            // ritorno alla wall activity
            [self performSegueWithIdentifier:@"goBackToWallFromCreateJournal" sender:nil];

        } else {
            // visualizzo un messaggio di errore
            NSString *message = @"Complete form.";
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                           message:message
                                                                    preferredStyle:UIAlertControllerStyleAlert];
            
            [self presentViewController:alert animated:YES completion:nil];
            
            int duration = 3; // duration in seconds
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
                [alert dismissViewControllerAnimated:YES completion:nil];
            });
        }
    } else {
        NSLog(@"CONNECTION NOT AVAILABLE");
        // visualizzo un messaggio di errore
        NSString *message = @"Impossible to complete the operation. Connection is not available. Please, turn on internet and retry the operation";
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                       message:message
                                                                preferredStyle:UIAlertControllerStyleAlert];
        
        [self presentViewController:alert animated:YES completion:nil];
        
        int duration = 3; // duration in seconds
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
    }
}

// chiude la tastiera all'inivio
-(IBAction)closeKeyboard:(id)sender{
    [sender resignFirstResponder];
}

// controllo label
-(bool) checkEmptyTextEdit : (UITextField*) field andErrorString : (NSString*) error{
    if (field.text && field.text.length == 0){
        NSString *message = error;
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                       message:message
                                                                preferredStyle:UIAlertControllerStyleAlert];
        
        [self presentViewController:alert animated:YES completion:nil];
        
        int duration = 1; // duration in seconds
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
        return false;
    }
    return true;
}

// controllo caratteri  speciali
-(bool) checkTextContainSpecialChar : (UITextField*) field andErrorString : (NSString*) error{
    NSCharacterSet * set = [[NSCharacterSet characterSetWithCharactersInString:@"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLKMNOPQRSTUVWXYZ0123456789"] invertedSet];
    if ([field.text rangeOfCharacterFromSet:set].location != NSNotFound){
        NSString *message = error;
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                       message:message
                                                                preferredStyle:UIAlertControllerStyleAlert];
        
        [self presentViewController:alert animated:YES completion:nil];
        
        int duration = 1; // duration in seconds
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
        return false;
    }
    return true;
}


// controllo sulle date
-(bool) checkIfDatesAreCorrect {
    if (!(_departureDate == _returnDate) && [_departureDate compare:_returnDate] == NSOrderedDescending){
        NSString *message = @"Departure date cannot be after return date";
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                    message:message
                                                    preferredStyle:UIAlertControllerStyleAlert];
        
        [self presentViewController:alert animated:YES completion:nil];
        
        int duration = 1; // duration in seconds
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
        return false;
    }
    return true;
}

// controllo se il nome del journal esiste già
-(bool) checkIfNameJournalArleadyExist : (UITextField*) field andOwnerID: (long long) ownerID andErrorString : (NSString*) error{
    Journal *journalFound = [[Journals getInstance] getJournal:[[JournalID alloc] initWithName:field.text andOwnerId:ownerID]];
    NSLog(@"%@",journalFound.name);
    if (journalFound != nil){
        NSString *message = error;
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                       message:message
                                                                preferredStyle:UIAlertControllerStyleAlert];
        
        [self presentViewController:alert animated:YES completion:nil];
        
        int duration = 1; // duration in seconds
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
        return false;
    }
    return true;
}
- (IBAction)inviteApp:(id)sender {
    FBSDKAppInviteContent *content =[[FBSDKAppInviteContent alloc] init];
    content.appLinkURL = [NSURL URLWithString:@"https://www.travelnotes.com/travelnotes"];
    //optionally set previewImageURL
    content.appInvitePreviewImageURL = [NSURL URLWithString:@"https://www.travelnotes.com/travelnotes.jpg"];
    
    // Present the dialog. Assumes self is a view controller
    // which implements the protocol FBSDKAppInviteDialogDelegate.
    [FBSDKAppInviteDialog showFromViewController:self
                                     withContent:content
                                        delegate:self];
}

- (void)appInviteDialog:(FBSDKAppInviteDialog *)appInviteDialog didCompleteWithResults:	(NSDictionary *)results{
    NSLog(@"Invites sended");
}

- (void)appInviteDialog:(FBSDKAppInviteDialog *)appInviteDialog didFailWithError:(NSError *)error {
    NSLog(@"Error with invites");
}
@end
