//
//  AppDelegate.m
//  progettomobidev
//
//  Created by gianpaolo on 28/09/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>

#import "AppDelegate.h"
#import "Journals.h"

@import GoogleMaps;
@import GooglePlaces;

@interface AppDelegate ()

@end

@implementation AppDelegate{
    NSUserDefaults *_preferences;
}


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // importo la chiave per poter usare Google Maps e le funzioni di Places
    [GMSServices provideAPIKey:@"AIzaSyAPSovmyPZq41Pv4BvnLfoFU57O5Xxfr0A"];
    [GMSPlacesClient provideAPIKey:@"AIzaSyDt-2qdxKKgZLJf9ejCl49hRDou5k9kMSc"];

    [[FBSDKApplicationDelegate sharedInstance] application:application
                             didFinishLaunchingWithOptions:launchOptions];
    [FBSDKLoginButton class];
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    [self saveContext];
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    // Saves changes in the application's managed object context before the application terminates.
    [self saveContext];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    return [[FBSDKApplicationDelegate sharedInstance] application:application
                                                          openURL:url
                                                sourceApplication:sourceApplication
                                                       annotation:annotation
            ];
}

-(void) saveContext{
    _preferences = [NSUserDefaults standardUserDefaults];
    NSString *lastJournalName = @"last_journal_name";
    NSString *lastJournalOwner = @"last_journal_owner";
    
    NSString *journalName = [[Journals getInstance] getCurrentJournal].name;
    long long journalOwnerID = [[Journals getInstance] getCurrentJournal].owner_id;
    
    [_preferences setValue:journalName forKey:lastJournalName];
    [_preferences setValue:[NSString stringWithFormat:@"%lld",journalOwnerID] forKey:lastJournalOwner];
    [_preferences synchronize];
    
    NSLog(@"Journal preferences update %@", [_preferences stringForKey:lastJournalName]);
}
@end
