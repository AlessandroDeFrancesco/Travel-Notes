//
//  ServerCalls.m
//  prova rest
//
//  Created by gianpaolo on 09/10/16.
//  Copyright © 2016 caprara. All rights reserved.
//

#import "ServerCalls.h"
#import "UNIRest.h"
#import "Utility.h"
#import "Journals.h"
#import "MemoryCacheUtility.h"

@implementation ServerCalls

+ (ServerCalls*)getInstance
{
    static ServerCalls *sharedServer = nil;
    @synchronized(self) {
        if (sharedServer == nil)
            sharedServer = [[self alloc] init];
    }
    return sharedServer;
}

- (void) downloadJournal: (void(^)(Journal*))journalCallback andJournalOwnerID : (long long) journalOwnerID andJournalName : (NSString*) journalName{
    _getJournalCallback = [journalCallback copy];

    NSLog(@"DOWNLOAD JOURNAL");
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    NSDictionary* headers = @{@"accept": @"application/json"};
    resourcePath= @"resources/journal/";
    
    NSString *user_id = [NSString stringWithFormat:@"%lld/", journalOwnerID];
    user_id = [user_id stringByAppendingString:journalName];
    resourcePath = [resourcePath stringByAppendingString:user_id];
    
    [[UNIRest get:^(UNISimpleRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            NSData *rawBody = response.rawBody;
            NSDictionary* jsonJournal = [NSJSONSerialization JSONObjectWithData: rawBody options:0 error:nil];
            
            
            NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
            [formatter setDateFormat: @"yyyy-MM-dd'T'HH:mm:ss"];
            Journal *newJournal;
            newJournal = [Journal alloc];
            //settare il journal
            
            NSDate* departureDate = [formatter dateFromString:[jsonJournal objectForKey:@"departureDate"]];
            NSDate* returnDate = [formatter dateFromString:[jsonJournal objectForKey:@"returnDate"]];
            
            [newJournal initWithName:[jsonJournal objectForKey:@"name"] andDescription:[jsonJournal objectForKey:@"description"] andType:[jsonJournal objectForKey:@"type"] andCity:[jsonJournal objectForKey:@"city"] andOwnerId:[[jsonJournal objectForKey:@"owner_id"] longLongValue] andDepartureDate:departureDate andReturnDate:returnDate];
            
            // aggiungo gli elementi al journal
            for (NSDictionary *elementJournal in [jsonJournal objectForKey:@"elements"]){
                NSLog(@"%@",elementJournal);
                Element* newElement;
                // verifico qual'è il tipo dell'element per settare quello corretto
                if ([[elementJournal objectForKey:@"type"] isEqualToString:@"NOTE"]){
                    newElement = [[Element alloc] initWithElementType:NOTE];
                } else if ([[elementJournal objectForKey:@"type"] isEqualToString:@"IMAGE"]){
                    newElement = [[Element alloc] initWithElementType:IMAGE];
                } else {
                    newElement = [[Element alloc] initWithElementType:VIDEO];
                }
                newElement.content = [elementJournal objectForKey:@"content"];
                newElement.ownerName = [elementJournal objectForKey:@"ownerName"];
                NSDate* contentDate = [formatter dateFromString:[elementJournal objectForKey:@"date"]];
                newElement.date = contentDate;
                newElement.idElement = [[elementJournal objectForKey:@"id"] intValue];
                
                newElement.latitude = [[elementJournal objectForKey:@"latitude"] floatValue];
                newElement.longitude = [[elementJournal objectForKey:@"longitude"] floatValue];
                
                [newJournal addElement:newElement];
            }
            
            // aggiungo i partecipanti
            for (NSNumber* participant in [jsonJournal objectForKey:@"participants"]){
                [newJournal addParticipant:participant];
            }
            
            _getJournalCallback(newJournal);
            
            // Clean up.
            _getJournalCallback = nil;
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
        }

    }];
    
}

- (void) downloadAllJournals: (void(^)(NSMutableArray*))journalsCallback andJournalOwnerID : (long long) journalOwnerID{
    _getJournalsCallback = [journalsCallback copy];
    
    NSLog(@"DOWNLOAD ALL JOURNALS %llu",journalOwnerID);
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    NSDictionary* headers = @{@"accept": @"application/json"};
    resourcePath= @"resources/journal/";

    NSString *user_id = [NSString stringWithFormat:@"%lld", journalOwnerID];
    
    resourcePath = [resourcePath stringByAppendingString:user_id];
    
    [[UNIRest get:^(UNISimpleRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            NSData *rawBody = response.rawBody;
            NSDictionary* jsonJournal = [NSJSONSerialization JSONObjectWithData: rawBody options:0 error:nil];
            // ottengo i journal singoli
            NSMutableArray *journalsList = [[NSMutableArray alloc] init];
            
            NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
            [formatter setDateFormat: @"yyyy-MM-dd'T'HH:mm:ss"];
            
            for (NSDictionary *journal in jsonJournal){
                Journal* newJournal;
                newJournal = [Journal alloc];
                
                NSDate* departureDate = [formatter dateFromString:[journal objectForKey:@"departureDate"]];
                NSDate* returnDate = [formatter dateFromString:[journal objectForKey:@"returnDate"]];
                
                [newJournal initWithName:[journal objectForKey:@"name"] andDescription:[journal objectForKey:@"description"] andType:[journal objectForKey:@"type"] andCity:[journal objectForKey:@"city"] andOwnerId:[[journal objectForKey:@"owner_id"] longLongValue] andDepartureDate:departureDate andReturnDate:returnDate];
                
                // aggiungo gli elementi al journal
                for (NSDictionary *elementJournal in [journal objectForKey:@"elements"]){
                    NSLog(@"%@",elementJournal);
                    Element* newElement;
                    // verifico qual'è il tipo dell'element per settare quello corretto
                    if ([[elementJournal objectForKey:@"type"] isEqualToString:@"NOTE"]){
                        newElement = [[Element alloc] initWithElementType:NOTE];
                    } else if ([[elementJournal objectForKey:@"type"] isEqualToString:@"IMAGE"]){
                        newElement = [[Element alloc] initWithElementType:IMAGE];
                    } else {
                        newElement = [[Element alloc] initWithElementType:VIDEO];
                    }
                    newElement.content = [elementJournal objectForKey:@"content"];
                    newElement.ownerName = [elementJournal objectForKey:@"ownerName"];
                    NSDate* contentDate = [formatter dateFromString:[elementJournal objectForKey:@"date"]];
                    newElement.date = contentDate;
                    newElement.idElement = [[elementJournal objectForKey:@"id"] intValue];
                    
                    newElement.latitude = [[elementJournal objectForKey:@"latitude"] floatValue];
                    newElement.longitude = [[elementJournal objectForKey:@"longitude"] floatValue];
                    
                    [newJournal addElement:newElement];
                }
                
                // aggiungo i partecipanti
                for (NSNumber* participant in [journal objectForKey:@"participants"]){
                    [newJournal addParticipant:participant];
                }
                
                [journalsList addObject:newJournal];
            }
            
            
            _getJournalsCallback(journalsList);
            
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
        }
        
    }];
    
}

- (void) downloadElement: (void(^)(Element*))elementCallback andJournalOwnerID :(long long) journalOwnerID andJournalName : (NSString*) journalName andElementPosition : (NSInteger*) element_position{
    _getElementCallback = [elementCallback copy];
    
    NSLog(@"DOWNLOAD ELEMENT");
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    NSDictionary* headers = @{@"accept": @"application/json"};
    resourcePath= @"resources/journal/";
    
    NSString *user_id = [NSString stringWithFormat:@"%lld/", journalOwnerID];
    user_id = [user_id stringByAppendingString:journalName];
    
    resourcePath = [resourcePath stringByAppendingString:user_id];
    resourcePath = [resourcePath stringByAppendingString:@"/"];
    resourcePath = [resourcePath stringByAppendingString:[NSString stringWithFormat:@"%d", *element_position]];
    NSLog(@"%@",resourcePath);
    
    [[UNIRest get:^(UNISimpleRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            NSData *rawBody = response.rawBody;
            
            NSDictionary* jsonElement = [NSJSONSerialization JSONObjectWithData: rawBody options:0 error:nil];
            
            NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
            [formatter setDateFormat: @"yyyy-MM-dd'T'HH:mm:ss"];
            
            // estrazione element
            Element* newElement;
            // verifico qual'è il tipo dell'element per settare quello corretto
            if ([[jsonElement objectForKey:@"type"] isEqualToString:@"NOTE"]){
                newElement = [[Element alloc] initWithElementType:NOTE];
            } else if ([[jsonElement objectForKey:@"type"] isEqualToString:@"IMAGE"]){
                newElement = [[Element alloc] initWithElementType:IMAGE];
            } else {
                newElement = [[Element alloc] initWithElementType:VIDEO];
            }
            newElement.content = [jsonElement objectForKey:@"content"];
            newElement.ownerName = [jsonElement objectForKey:@"ownerName"];
            NSDate* contentDate = [formatter dateFromString:[jsonElement objectForKey:@"date"]];
            newElement.date = contentDate;
            newElement.idElement = [[jsonElement objectForKey:@"id"] intValue];
            newElement.latitude = [[jsonElement objectForKey:@"latitude"] floatValue];
            newElement.longitude = [[jsonElement objectForKey:@"longitude"] floatValue];
            
            elementCallback(newElement);
            _getElementCallback = nil;
            
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
        }
        
    }];
    
}

-(void) uploadElementToJournal: (void(^)(bool))addElementCallback andNewElement : (Element*) newElement andJournalOwnerID: (long long) journalOwnerID andJournalName : (NSString*) journalName andElementOwnerID : (NSString*) elementOwnerID{
    _addElementCallback = [addElementCallback copy];
    
    NSLog(@"UPLOAD ELEMENT");
    
    if(newElement.type != NOTE){
        // converto il contenuto dell'elemento in base64
        newElement = [MemoryCacheUtility convertElementContentToBase64:newElement];
    }
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    
    NSDictionary* headers = @{@"accept": @"text/plain" , @"Content-Type" : @"application/json"};
    NSDictionary* elementDict = [[[Utility alloc] init] dictionaryWithPropertiesOfElement:newElement];
    
    resourcePath= @"resources/journal/element/";
    
    NSString *user_id = [NSString stringWithFormat:@"%lld/", journalOwnerID];
    user_id = [user_id stringByAppendingString:journalName];
    
    resourcePath = [resourcePath stringByAppendingString:user_id];
    resourcePath = [resourcePath stringByAppendingString:@"/"];
    resourcePath = [resourcePath stringByAppendingString:elementOwnerID];
   
    if([elementDict[@"content"] length] > 50)
        NSLog(@"%@ id:%@ content: %@", elementDict[@"type"], elementDict[@"idElement"], [elementDict[@"content"] substringToIndex:50]);
    else
        NSLog(@"%@ content: %@", elementDict[@"type"], elementDict[@"content"]);
    
    [[UNIRest postEntity:^(UNIBodyRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
        // Converting NSDictionary to JSON:
        [request setBody:[NSJSONSerialization dataWithJSONObject:elementDict options:0 error:nil]];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            //NSData *rawBody = response.rawBody;
            //NSDictionary* jsonElement = [NSJSONSerialization JSONObjectWithData: rawBody options:0 error:nil];
            
            _addElementCallback(true);
            _addElementCallback = nil;
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
            _addElementCallback(false);
            _addElementCallback = nil;
        }
        
    }];

}

- (void) uploadNewJournal : (void(^)(bool))newJournalCallback andNewJournal : (Journal*) newJournal{
    _newJournalCallback = [newJournalCallback copy];
    NSLog(@"UPLOAD JOURNAL");
    
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    
    NSDictionary* headers = @{@"accept": @"text/plain" , @"Content-Type" : @"application/json"};
    NSDictionary* journalDict = [[[Utility alloc] init] dictionaryWithPropertiesOfJournal:newJournal];

    resourcePath= @"resources/journal/";
    
    [[UNIRest postEntity:^(UNIBodyRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
        // Converting NSDictionary to JSON:
        [request setBody:[NSJSONSerialization dataWithJSONObject:journalDict options:0 error:nil]];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            _newJournalCallback(true);
            _newJournalCallback = nil;
            
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
            _newJournalCallback(true);
            _newJournalCallback = nil;
        }
        
    }];
    
}

- (void) updateJournal : (void(^)(bool))updateJournalCallback andNewJournal: (Journal*) newJournal{
    _updateJournalCallback = [updateJournalCallback copy];
    NSLog(@"UPDATE JOURNAL");
    
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    
    NSDictionary* headers = @{@"accept": @"text/plain" , @"Content-Type" : @"application/json"};
    NSDictionary* journalDict = [[[Utility alloc] init] dictionaryWithPropertiesOfJournal:newJournal];
    resourcePath= @"resources/update_journal/";
    NSLog(@"DICT TO UPLOAD: %@",journalDict);
    [[UNIRest postEntity:^(UNIBodyRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
        // Converting NSDictionary to JSON:
        [request setBody:[NSJSONSerialization dataWithJSONObject:journalDict options:0 error:nil]];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            bool b = true;
            _updateJournalCallback(b);
            _updateJournalCallback = nil;
            
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
        }
        
    }];
}


- (void) addToken : (void(^)(bool))addTokenCallback andIdFacebook : (NSString*) idFacebook andToken : (NSString*) idToken{
    _addTokenCallback = [addTokenCallback copy];
    NSLog(@"ADD TOKEN");
    
    urlBaseString= @"http://travel-notes.herokuapp.com/";
    
    NSDictionary* headers = @{@"accept": @"text/plain" , @"Content-Type" : @"application/json"};
    
    resourcePath= @"resources/addtoken/";
    
    resourcePath = [resourcePath stringByAppendingString:idFacebook];
    resourcePath = [resourcePath stringByAppendingString:@"/"];
    resourcePath = [resourcePath stringByAppendingString:idToken];
    
    [[UNIRest postEntity:^(UNIBodyRequest *request) {
        [request setUrl:[urlBaseString stringByAppendingString:resourcePath]];
        [request setHeaders:headers];
    }] asJsonAsync:^(UNIHTTPJsonResponse* response, NSError *error) {
        // This is the asyncronous callback block
        if(response.code == 200){
            // implementare aggiunta token (se ci servirà)
            bool b = true;
            _addTokenCallback(b);
            _addTokenCallback = nil;
        } else {
            NSLog(@"An error accurred. Error %ld", (long)response.code);
        }
        
    }];
}

@end
