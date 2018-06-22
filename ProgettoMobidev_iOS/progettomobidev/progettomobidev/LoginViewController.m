//
//  ViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 28/09/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "LoginViewController.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>
#import "ServerCalls.h"
#import "Journals.h"
#import "UIViewController+MMDrawerController.h"
#import "LoadingPageView.h"
#import "Utility.h"

@interface LoginViewController (){
    UIView *_loadingView;
    NSUserDefaults *_preferences;
}
@end

@implementation LoginViewController

- (void)viewDidLoad {
    [self.navigationController setNavigationBarHidden:YES];
    [super viewDidLoad];

    FBSDKLoginButton *loginButton = [[FBSDKLoginButton alloc] init];
    
    _loadingView = [[[NSBundle mainBundle] loadNibNamed:@"LoadingPageView" owner:self options:nil] objectAtIndex:0];
    _loadingView.frame = self.view.frame;

    loginButton.delegate = self;
    
    [FBSDKProfile enableUpdatesOnAccessTokenChange:YES];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(profileUpdated:) name:FBSDKProfileDidChangeNotification object:nil];

    // setto i permessi
    self.loginButton.readPermissions = @[@"public_profile", @"user_friends"];	
    
    // metto il login button al centro della view
    loginButton.center = self.view.center;

    // setto il pulsante alla view
    [self.view addSubview:loginButton];

    
    // verifica se l'accesso è già stato effettuato
    if ([FBSDKAccessToken currentAccessToken]) {
        NSLog(@"Login già effettuato.");
        [self.view addSubview:_loadingView];
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self downloadJournals];

        });
    }
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


-(void)profileUpdated:(NSNotification *) notification{
    NSLog(@"User name: %@",[FBSDKProfile currentProfile].name);
    NSLog(@"User ID: %@",[FBSDKProfile currentProfile].userID);
}

- (void)loginButton:(FBSDKLoginButton *)loginButton
didCompleteWithResult:(FBSDKLoginManagerLoginResult *)result
              error:(NSError *)error {
    if (error) {
        NSLog(@"Process error. %@", error);
    }
    else if (result.isCancelled) {
        NSLog(@"Cancelled.");
    }
    else {
        NSLog(@"Logged in.");
        [self.view addSubview:_loadingView];
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            //Aspetto 2 secondi in maniera tale da far aggiornare il profile facebook
            [NSThread sleepForTimeInterval:2.0f];
            dispatch_async(dispatch_get_main_queue(), ^{
                [self downloadJournals];
            });
        });
    }
}

// utilizzato per prepararsi al passaggio da un VC ad un altro
- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([segue.identifier isEqualToString:@"fromLoginToWall"]){
        
        [_loadingView removeFromSuperview];

        // setto il drawer
        MMDrawerController *destinationViewController = (MMDrawerController *) segue.destinationViewController;
        
        [destinationViewController setMaximumLeftDrawerWidth:250.0];
        destinationViewController.closeDrawerGestureModeMask = MMCloseDrawerGestureModeTapCenterView;
        
        // Instantitate and set the center view controller.
        UIViewController *centerViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"MAIN_NAVIGATION_CONTROLLER"];
        [destinationViewController setCenterViewController:centerViewController];
            
        // Instantiate and set the left drawer controller.
        UIViewController *leftDrawerViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DrawerViewController"];
        [destinationViewController setLeftDrawerViewController:leftDrawerViewController];
        
    }
}


-(void) downloadJournals{
    if ([Utility isNetworkAvailable]){
        NSLog(@"Scaricamento journal utente");
        ServerCalls* serverCalls = [ServerCalls getInstance];
        // converto lo UserID in un long long
        NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
        [f setNumberStyle:NSNumberFormatterDecimalStyle];
        NSNumber * fbIDValue = [f numberFromString:[FBSDKProfile currentProfile].userID];
        
        [serverCalls downloadAllJournals:^(NSMutableArray *journalsList) {
            Journals *journals = [Journals getInstance];
            [journals setJournalsInDict:journalsList];
            NSLog(@"Scaricamento completato");
            // se i journal sono 0 cancello il riferimento all'ultimo journal aperto
            if ([[Journals getInstance] getJournals].count == 0){
                _preferences = [NSUserDefaults standardUserDefaults];
                NSString *lastJournalName = @"last_journal_name";
                NSString *lastJournalOwner = @"last_journal_owner";
                [_preferences setValue:nil forKey:lastJournalName];
                [_preferences setValue:nil forKey:lastJournalOwner];
                [_preferences synchronize];
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                [self performSegueWithIdentifier:@"fromLoginToWall" sender:self];
            });
        } andJournalOwnerID:[fbIDValue longLongValue]];
    } else {
        UIAlertController *errorController = [UIAlertController alertControllerWithTitle:@"Error" message:@"Error with server or check your internet connection.\nDo you want retry the operation?" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* yesButton = [UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            // ripete l'operazione
            [self downloadJournals];
        }];
        
        UIAlertAction* noButton = [UIAlertAction actionWithTitle:@"No" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            exit(0);
        }];
        
        [errorController addAction:yesButton];
        [errorController addAction:noButton];
        
        [self presentViewController:errorController animated:YES completion:nil];
    }
}

@end
